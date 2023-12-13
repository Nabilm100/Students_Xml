package com.example.assign1.Controller;
import com.example.assign1.Entity.Student;
import com.example.assign1.Services.StudentXmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/students")
public class StudentController {

    private StudentXmlService studentXmlService;

    @Autowired
    public StudentController(StudentXmlService studentXmlService) {
        this.studentXmlService = studentXmlService;
    }

    @PostMapping(value = "/save")
    public ResponseEntity<?> saveStudentToXml(@RequestBody Student student) {
        Student savedStudent = studentXmlService.saveStudentToXml(student);

        if (savedStudent != null) {
            // Return the saved student if successful
            return ResponseEntity.ok(savedStudent);
        } else {
            // Return an error response if the save operation failed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid Data");
        }
    }



   // @PostMapping(value = "/save")
   // public Student saveStudentToXml(@RequestBody Student student) {return studentXmlService.saveStudentToXml(student);}

    //-------------------------NEW FEATURES-------------------------

    @PostMapping(value = "/update")
    public Student updateStudentDetails(@RequestBody Student student) {
        return studentXmlService.updateStudentDetails(student);
    }

    @GetMapping("/sort")
    public List<Student> sortStudents(
            @RequestParam String sortBy,
            @RequestParam(required = false, defaultValue = "true") boolean ascending) {
        return studentXmlService.sortStudents(sortBy, ascending);
    }





     //---------------------END-------------------------


    @GetMapping(value = "/getAllStudents")
    public List<Student> getAllStudentsFromXml() {
        return studentXmlService.getAllStudentsFromXml();

    }



    @GetMapping(value = "/getById/{id}")
    public Student getStudentByIdFromXml(@PathVariable String id) {
        return studentXmlService.getStudentByIdFromXml(id);
    }

   // @GetMapping(value = "/searchfirst/{firstName}")
    //public List<Student> searchStudentsByFirstNameFromXml(@PathVariable String firstName) {return studentXmlService.searchStudentsByFirstNameFromXml(firstName);}
   @GetMapping(value = "/searchfirst/{firstName}")
   public Map<String, Object> searchStudentsByFirstNameFromXml(@PathVariable String firstName) {
       List<Student> students = studentXmlService.searchStudentsByFirstNameFromXml(firstName);
       int numberOfStudents = students.size();

       Map<String, Object> response = new HashMap<>();
       response.put("students", students);
       response.put("numberOfStudents", numberOfStudents);

       return response;
   }
    @GetMapping(value = "/searchlast/{lastName}")
    public List<Student> searchStudentsByLastNameFromXml(@PathVariable String lastName) {
        return studentXmlService.searchStudentsByLastNameFromXml(lastName);
    }

    @GetMapping(value = "/searchgender/{gender}")
    public Map<String, Object> searchStudentsByGender(@PathVariable String gender) {
        List<Student> students =  studentXmlService.searchStudentsByGender(gender);
        int numberOfStudents = students.size();

        Map<String, Object> response = new HashMap<>();
        response.put("students", students);
        response.put("numberOfStudents", numberOfStudents);

        return response;
    }

    @GetMapping(value = "/searchaddress/{address}")
    public List<Student> searchStudentsByAddress(@PathVariable String address) {
        return studentXmlService.searchStudentsByAddress(address);
    }




    @GetMapping("/searchlevel/{level}")
    public ResponseEntity<List<Student>> searchStudentsByLevel(@PathVariable Integer level) {
        List<Student> matchingStudents = studentXmlService.searchStudentsByLevel(level);

        if (!matchingStudents.isEmpty()) {
            return new ResponseEntity<>(matchingStudents, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/searchgpa")
    public ResponseEntity<List<Student>> searchStudentsByGPA(@RequestParam(name = "gpa") Double gpa) {
        List<Student> matchingStudents = studentXmlService.searchStudentsByGPA(gpa);

        if (!matchingStudents.isEmpty()) {
            return new ResponseEntity<>(matchingStudents, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/delete/{id}")
    public void deleteStudentByIdFromXml(@PathVariable String id) {
        studentXmlService.deleteStudentById(id);
    }
}


