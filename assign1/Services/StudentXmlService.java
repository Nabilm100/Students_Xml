package com.example.assign1.Services;

import com.example.assign1.Entity.Student;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StudentXmlService {
    private static final String XML_FILE_PATH = "D:\\download_here\\assign1\\target\\classes\\com\\example\\assign1\\student\\Data.xml";






    private static String getElementText(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }


    //--------------------------


    public List<Student> getAllStudentsFromXml() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Load the input XML document, parse it and return an instance of the
            // Document class.
            Document document = builder.parse(new File(XML_FILE_PATH));

            List<Student> students = new ArrayList<>();
            NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;

                    // Get the value of the ID attribute.
                    String ID = node.getAttributes().getNamedItem("ID").getNodeValue();

                    // Get the value of all sub-elements.
                    String firstname = elem.getElementsByTagName("FirstName")
                            .item(0).getChildNodes().item(0).getNodeValue();

                    String lastname = elem.getElementsByTagName("LastName").item(0)
                            .getChildNodes().item(0).getNodeValue();

                    String gender = elem.getElementsByTagName("Gender").item(0)
                            .getChildNodes().item(0).getNodeValue();

                    double gpa = Double.parseDouble(elem.getElementsByTagName("GPA").item(0)
                            .getChildNodes().item(0).getNodeValue());

                    int level = Integer.parseInt(elem.getElementsByTagName("Level").item(0)
                            .getChildNodes().item(0).getNodeValue());

                    String address = elem.getElementsByTagName("Address").item(0)
                            .getChildNodes().item(0).getNodeValue();

                    students.add(new Student(ID, firstname, lastname, gender, gpa, level, address));
                }
            }
            return students;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public Student saveStudentToXml(Student newStudent) {
        if(!validationData(newStudent)){
            System.out.println("Invalid Data");
            return null;}

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document;
            File file = new File(XML_FILE_PATH);

            if (file.exists()) {
                document = builder.parse(file);
            } else {
                document = builder.newDocument();
                Element rootElement = document.createElement("University");
                document.appendChild(rootElement);
            }

            Element studentElement = createStudentElement(document, newStudent);
            document.getDocumentElement().appendChild(studentElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(file));

             return newStudent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean validationData(Student st){

        List<Student> students = getAllStudentsFromXml();
        for (Student student : students) {
            if (student.getId().equals(st.getId())) {
                return false;
            }
        }
        if(st.getGpa()<0 || st.getGpa()>4){
            return false;
        }

        Pattern pattern = Pattern.compile("^[a-z]+$");
        boolean matches = pattern.matcher(st.getAddress()).matches();
        if(!matches){
            return false;
        }

        if(st.getLevel() > 4 || st.getLevel()<0){
            return false;
        }

        return(validateString(st.getGender()) && validateString(st.getFirstName()) && validateString(st.getLastName()) && validateString(st.getId()) && validateString(st.getAddress()));



    }

    private boolean validateString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public Student updateStudentDetails(Student updatedStudent) {
        List<Student> students = getAllStudentsFromXml();
        String id = updatedStudent.getId();

        for (Student student : students) {
            if (student.getId().equals(id)) {
                // Update the provided fields
                if (updatedStudent.getFirstName() != null) {
                    student.setFirstName(updatedStudent.getFirstName());
                }
                if (updatedStudent.getLastName() != null) {
                    student.setLastName(updatedStudent.getLastName());
                }
                if (updatedStudent.getGender() != null) {
                    student.setGender(updatedStudent.getGender());
                }
                if (updatedStudent.getGpa() >= 0 && updatedStudent.getGpa() <= 4) {
                    student.setGpa(updatedStudent.getGpa());
                }
                if (updatedStudent.getLevel() >= 0 && updatedStudent.getLevel() <= 4) {
                    student.setLevel(updatedStudent.getLevel());
                }
                if (updatedStudent.getAddress() != null) {
                    student.setAddress(updatedStudent.getAddress());
                }

                //nseeeeeeeeet
           //   if(!validationData(student)){return null;}

                // Save the updated list back to XML (overwrite the file)
                saveStudentsToXml(students);

                return student; // Return the updated student
            }
        }

        return null; // If no matching student was found
    }





    public List<Student> sortStudents(@RequestParam String sortBy, @RequestParam boolean ascending) {
        List<Student> students = getAllStudentsFromXml();

        students.sort((s1, s2) -> {
            switch (sortBy.toLowerCase()) {
                case "id":
                    int id1 = Integer.parseInt(s1.getId());
                    int id2 = Integer.parseInt(s2.getId());
                    return Integer.compare(id1, id2);
                case "firstname":
                    return s1.getFirstName().compareTo(s2.getFirstName());
                case "lastname":
                    return s1.getLastName().compareTo(s2.getLastName());
                case "gender":
                    return s1.getGender().compareTo(s2.getGender());
                case "gpa":
                    return Double.compare(s1.getGpa(), s2.getGpa());
                case "level":
                    return Integer.compare(s1.getLevel(), s2.getLevel());
                case "address":
                    return s1.getAddress().compareTo(s2.getAddress());


                default:
                    return 0; // Default case, no sorting
            }
        });

        if (!ascending) {
            Collections.reverse(students); // Reverse the list for descending order
        }

        // Save the sorted list back to XML (overwrite the file)
        saveStudentsToXml(students);

        return students;
    }





    public Student getStudentByIdFromXml(@PathVariable String id) {
        List<Student> students = getAllStudentsFromXml();

        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;

    }



    public List<Student> searchStudentsByLevel(Integer level) {
        List<Student> allStudents = getAllStudentsFromXml();

        if (level != null) {
            // Use Java Stream to filter students based on level
            List<Student> matchingStudents = allStudents.stream()
                    .filter(student -> level.equals(student.getLevel()))
                    .collect(Collectors.toList());

            return matchingStudents;
        } else {
            // Handle the case when 'level' is null (e.g., return an error, log it, etc.)
            // For now, returning an empty list as an example
            return Collections.emptyList();
        }
    }

    public List<Student> searchStudentsByGPA(Double gpa) {
        List<Student> allStudents = getAllStudentsFromXml();

        if (gpa != null) {
            // Use Java Stream to filter students based on GPA
            List<Student> matchingStudents = allStudents.stream()
                    .filter(student -> gpa.equals(student.getGpa()))
                    .collect(Collectors.toList());

            return matchingStudents;
        } else {
            // Handle the case when 'gpa' is null (e.g., return an error, log it, etc.)
            // For now, returning an empty list as an example
            return Collections.emptyList();
        }
    }



    public List<Student> searchStudentsByGender(@PathVariable String gender){
        List<Student> students = getAllStudentsFromXml();
        List<Student> matchingStudents = new ArrayList<>();

        for (Student student : students) {
            if (student.getGender().equalsIgnoreCase(gender)) {
                matchingStudents.add(student);
            }
        }

        return matchingStudents;
    }

    public List<Student> searchStudentsByAddress(@PathVariable String address){
        List<Student> students = getAllStudentsFromXml();
        List<Student> matchingStudents = new ArrayList<>();

        for (Student student : students) {
            if (student.getAddress().equalsIgnoreCase(address)) {
                matchingStudents.add(student);
            }
        }

        return matchingStudents;
    }




    public List<Student> searchStudentsByFirstNameFromXml(@PathVariable String firstName){
        List<Student> students = getAllStudentsFromXml();
        List<Student> matchingStudents = new ArrayList<>();

        for (Student student : students) {
            if (student.getFirstName().equalsIgnoreCase(firstName)) {
                matchingStudents.add(student);
            }
        }

        return matchingStudents;
    }


    public List<Student> searchStudentsByLastNameFromXml(@PathVariable String lastName){
        List<Student> students = getAllStudentsFromXml();
        List<Student> matchingStudents = new ArrayList<>();
        for (Student student : students) {
            if (student.getLastName().equalsIgnoreCase(lastName)) {
                matchingStudents.add(student);
            }
        }

        return matchingStudents;
    }


    public boolean deleteStudentById(@PathVariable String id) {
        List<Student> students = getAllStudentsFromXml();

        // Iterate through the list of students
        for (Student student : students) {
            if (student.getId().equals(id)) {
                // Remove the matching student
                students.remove(student);
                // Save the updated list back to XML (overwrite the file)
                saveStudentsToXml(students);
                // Return true indicating deletion success
                return true;
            }
        }

        // If no matching student was found, return false
        return false;
    }


    //------------------------------

    public void saveStudentsToXml(List<Student> students) {
        try {
            // Load the existing XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(XML_FILE_PATH));

            // Get the root element (assuming it's <University>)
            Element universityElement = doc.getDocumentElement();

            // Remove existing student elements
            NodeList existingStudents = universityElement.getElementsByTagName("Student");
            for (int i = existingStudents.getLength() - 1; i >= 0; i--) {
                Node studentNode = existingStudents.item(i);
                universityElement.removeChild(studentNode);
            }

            // Add the updated list of students
            for (Student student : students) {
                // Create a new XML element for each student
                Element studentElement = doc.createElement("Student");

                // Set attributes or elements for the student
                studentElement.setAttribute("ID", student.getId());

                // Create and append elements for other properties
                studentElement.appendChild(createElement(doc, "FirstName", student.getFirstName()));
                studentElement.appendChild(createElement(doc, "LastName", student.getLastName()));
                studentElement.appendChild(createElement(doc, "Gender", student.getGender()));
                studentElement.appendChild(createElement(doc, "GPA", String.valueOf(student.getGpa())));
                studentElement.appendChild(createElement(doc, "Level", String.valueOf(student.getLevel())));
                studentElement.appendChild(createElement(doc, "Address", student.getAddress()));

                // Append the student element to the root (University) element
                universityElement.appendChild(studentElement);
            }

            // Save the updated XML back to the file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(XML_FILE_PATH));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | IOException | org.xml.sax.SAXException | javax.xml.transform.TransformerException e) {
            // Handle exceptions (e.g., log the error or throw a custom exception)
            e.printStackTrace();
        }
    }

    // Helper method to create an XML element with a text value
    private Element createElement(Document doc, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(textContent));
        return element;
    }






    // ... other methods ...



    private Element createStudentElement(Document document, Student student) {
        Element studentElement = document.createElement("Student");
        studentElement.setAttribute("ID", student.getId());

        appendChildWithTextContent(document, studentElement, "FirstName", student.getFirstName());
        appendChildWithTextContent(document, studentElement, "LastName", student.getLastName());
        appendChildWithTextContent(document, studentElement, "Gender", student.getGender());
        appendChildWithTextContent(document, studentElement, "GPA", String.valueOf(student.getGpa()));
        appendChildWithTextContent(document, studentElement, "Level", String.valueOf(student.getLevel()));
        appendChildWithTextContent(document, studentElement, "Address", student.getAddress());

        return studentElement;
    }



    private void appendChildWithTextContent(Document document, Element parentElement, String childTagName, String textContent) {
        Element childElement = document.createElement(childTagName);
        childElement.setTextContent(textContent);
        parentElement.appendChild(childElement);
    }

}
