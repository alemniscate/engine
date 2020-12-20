package engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.time.*;
import java.time.format.*;

@SpringBootApplication
@RestController
public class WebQuizEngine {

    static long id = 0;

    @Autowired
    private QuizDbService qdbs;

    @Autowired
    private CompletedDbService cdbs;

    public static void main(String[] args) {
        SpringApplication.run(WebQuizEngine.class, args);
    }
  
    @GetMapping("/api/quizzes/{id}") 
    public Quiz getQuiz(@PathVariable String id) {
        QuizDb quizDb = getQuizDb(id);
        Quiz quiz = new Quiz(quizDb.getId(), quizDb.getTitle(), quizDb.getText(), quizDb.getOptionsArray());
        return quiz;
    }
  
    QuizNavi buildQuizNavi(Integer pageNo) {
        List<Quiz> quizList = new ArrayList<>();
        List<QuizDb> quizDbList = qdbs.findQuizDb(pageNo);
        for (QuizDb quizDb: quizDbList) {
            quizList.add(new Quiz(quizDb.getId(), quizDb.getTitle(), quizDb.getText(), quizDb.getOptionsArray()));
        }
  
        int number = pageNo;
        List<Quiz> content = quizList;
        int size = 10;
        int totalElements = Math.toIntExact(qdbs.count());
        int totalPages = (totalElements + size - 1) / size;
        int numberOfElements = content.size();
        boolean empty = content.isEmpty();
    
        return new QuizNavi(totalPages, totalElements, number, numberOfElements, size, empty, content);
    }

    @GetMapping("/api/quizzes") 
    public QuizNavi getQuizzes(@RequestParam(name = "page", defaultValue = "0") Integer pageNo) {
          return buildQuizNavi(pageNo);
    }
  
    CompletedNavi buildCompletedNavi(Integer pageNo, String user) {
        List<Completed> completedList = new ArrayList<>();
        List<CompletedDb> completedDbList = cdbs.findCompletedDb(pageNo, user);
        for (CompletedDb completedDb: completedDbList) {
            completedList.add(new Completed(completedDb.getId(), completedDb.getCompletedAtString()));
        }
  
        int number = pageNo;
        List<Completed> content = completedList;
        int size = 10;
        int totalElements = Math.toIntExact(cdbs.count(user));
        int totalPages = (totalElements + size - 1) / size;
        int numberOfElements = content.size();
        boolean empty = content.isEmpty();
    
        return new CompletedNavi(totalPages, totalElements, number, numberOfElements, size, empty, content);
    }

    @GetMapping("/api/quizzes/completed") 
    public CompletedNavi getQuizzesCompleted(@RequestParam(name = "page", defaultValue = "0") Integer pageNo, @RequestHeader Map<String, String> headers) {
        String user = getLoginUser(headers);
        return buildCompletedNavi(pageNo, user);
    }
    
    String getLoginUser(Map<String, String> headers) {
        String key = "authorization";
        String value = headers.get(key);
        if (!value.startsWith("Basic ")) {
            return "";
        }
        byte[] bytes = Base64.getDecoder().decode(value.substring(6));
        String str = new String(bytes);
        String[] strs = str.split(":");
        if (strs.length < 2) {
            return "";
        }
        return strs[0];
    }

    @PostMapping(value = "/api/quizzes", consumes = "application/json") 
    public Quiz createQuiz(@RequestBody NewQuiz newQuiz, @RequestHeader Map<String, String> headers) {
        String owner = getLoginUser(headers);
        if ("".equals(owner)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");          
        }
        if (!newQuiz.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");          
        }
        id++;
        int[] answer = new int[0];
        if (!newQuiz.isAnswerNull()) {
            answer = newQuiz.getAnswer();
        }
        QuizDb quizDb = new QuizDb(id, newQuiz.getTitle(), newQuiz.getText(), newQuiz.getOptions(), answer, owner);
        qdbs.save(quizDb);
        return new Quiz(id, newQuiz.getTitle(), newQuiz.getTitle(), newQuiz.getOptions());
    }

    QuizDb getQuizDb(String idStr) {
        long id = Long.parseLong(idStr);
        Optional<QuizDb> quizDbNullable = qdbs.findQuizDb(id);
        if (!quizDbNullable.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "");   
        }
        return quizDbNullable.get();
    }

    void saveCompletedDb(String user, String quizIdStr) {
        long quizId = Long.parseLong(quizIdStr);
/*
        Optional<CompletedDb> completedDbNullable = cdbs.findCompletedDb(user, quizId);
        CompletedDb completedDb;
        if (!completedDbNullable.isPresent()) {
            completedDb = new CompletedDb(user, quizId);
        } else {
            completedDb = completedDbNullable.get();
            completedDb.setCompletedAt(LocalDateTime.now());
        }
*/
        CompletedDb completedDb = new CompletedDb(user, quizId);
        cdbs.save(completedDb);
    }

    @PostMapping(value = "/api/quizzes/{id}/solve") 
    public Judge getAnswer(@PathVariable String id, @RequestBody Answer answer, @RequestHeader Map<String, String> headers) {
        String user = getLoginUser(headers);

        QuizDb quizDb = getQuizDb(id);

        Judge judge = new Judge();
        
        if (answer.equals(quizDb.getAnswerArray())) {
            judge.setSuccess(true);
            judge.setFeedback("Congratulations, you're right!");
            saveCompletedDb(user, id);
        } else {
            judge.setSuccess(false);
            judge.setFeedback("Wrong answer! Please, try again.");
        }

        return judge;
    }

    @DeleteMapping("/api/quizzes/{id}")
    public void deleteQuiz(@PathVariable String id, @RequestHeader Map<String, String> headers) {
        QuizDb quizDb = getQuizDb(id);
        String owner = getLoginUser(headers);
        if ("".equals(owner)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "");          
        }
        if (!owner.equals(quizDb.getOwner())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "");          
        }
        qdbs.delete(quizDb.getId());
        throw new ResponseStatusException(HttpStatus.NO_CONTENT, "");                  
    }
}

class CompletedNavi {
/**
{
  "totalPages":1,
  "totalElements":5,
  "last":true,
  "first":true,
  "empty":false,
  "content":[
    {"id":103,"completedAt":"2019-10-29T21:13:53.779542"},
    {"id":102,"completedAt":"2019-10-29T21:13:52.324993"},
    {"id":101,"completedAt":"2019-10-29T18:59:58.387267"},
    {"id":101,"completedAt":"2019-10-29T18:59:55.303268"},
    {"id":202,"completedAt":"2019-10-29T18:59:54.033801"}
  ]
}
*/
    private int totalPages;
    private int totalElements;
    private int number;
    private int numberOfElements;
    private int size;
    private boolean empty;
    private List<Completed> content;

    public CompletedNavi() {}

    public CompletedNavi(int totalPages, int totalElements, int number, int numberOfElements, int size, boolean empty, List<Completed> content) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.number = number;
        this.numberOfElements = numberOfElements;
        this.size = size;
        this.empty = empty;
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getNumber() {
        return number;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    } 

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return empty;
    }

    public List<Completed> getContent() {
        return content;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty; 
    }

    public void setContent(List<Completed> content) {
        this.content = content;
    }
}

class Completed {
/**    
    {"id":103,"completedAt":"2019-10-29T21:13:53.779542"}
*/
    long id;
    String completedAt;

    public Completed() {}

    public Completed(long id, String completedAt) {
        this.id = id;
        this.completedAt = completedAt;
    }

    public long getId() {
        return id;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setComletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}

class QuizNavi {
/**
    {
        "totalPages":1,
        "totalElements":3,
        "last":true,
        "first":true,
        "sort":{ },
        "number":0,
        "numberOfElements":3,
        "size":10,
        "empty":false,
        "pageable": { },
        "content":[
          {"id":102,"title":"Test 1","text":"Text 1","options":["a","b","c"]},
          {"id":103,"title":"Test 2","text":"Text 2","options":["a", "b", "c", "d"]},
          {"id":202,"title":"The Java Logo","text":"What is depicted on the Java logo?",
           "options":["Robot","Tea leaf","Cup of coffee","Bug"]}
        ]
      }
*/
    private int totalPages;
    private int totalElements;
    private int number;
    private int numberOfElements;
    private int size;
    private boolean empty;
    private List<Quiz> content;

    public QuizNavi() {}

    public QuizNavi(int totalPages, int totalElements, int number, int numberOfElements, int size, boolean empty, List<Quiz> content) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.number = number;
        this.numberOfElements = numberOfElements;
        this.size = size;
        this.empty = empty;
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getNumber() {
        return number;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    } 

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return empty;
    }

    public List<Quiz> getContent() {
        return content;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public void setContent(List<Quiz> content) {
        this.content = content;
    }
}
    
class Quiz {
/*
{
  "id": 1,
  "title": "Coffee drinks",
  "text": "Select only coffee drinks.",
  "options": ["Americano","Tea","Cappuccino","Sprite"]
}
*/
    private long id;
    private String title;
    private String text;
    private String[] options;
    
    public Quiz() {}
    
    public Quiz(long id, String title, String text, String[] options) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.options = options;
    } 
        
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    
    public String getText() {
        return text;
    }
    
    public String[] getOptions() {
        return options;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setOptions(String[] options) {
        this.options = options;
    }
}
    
class NewQuiz {
    /*
    {
        "title": "The Java Logo",
        "text": "What is depicted on the Java logo?",
        "options": ["Robot","Tea leaf","Cup of coffee","Bug"],
        "answer": [0,2]
    }
    */
    private String title;
    private String text;
    private String[] options;
    private int[] answer;
    
    public NewQuiz() {}
    
    public NewQuiz(String title, String text, String[] options, int[] answer) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
    } 
    
    public String getTitle() {
        return title;
    }
    
    public String getText() {
        return text;
    }
    
    public String[] getOptions() {
        return options;
    }
    
    public int[] getAnswer() {
        return answer;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setOptions(String[] options) {
        this.options = options;
    }
   
    public void setAnswer(int[] answer) {
        this.answer = answer;
    }

    boolean isValid() {
        if (title == null || text == null || options == null) {
            return false;
        }
        return true;
    }

    boolean isAnswerNull() {
        if (answer == null) {
            return true;
        }
        return false;
    }
}

class Answer {
/**
{
    "answer": [0,2]
}
*/
    private int[] answer;

    public Answer() {}

    public Answer(int[] answer) {
        this.answer = answer; 
    }

    public int[] getAnswer() {
        return answer;
    }

    public void setAnswer(int[] answer) {
        this.answer = answer;
    }

    public boolean equals(int[] other) {
        if (answer.length != other.length) {
            return false;
        }
        for (int i = 0; i < answer.length; i++) {
            if (answer[i] != other[i]) {
                return false;
            }
        }
        return true;
    }
}

class Judge {
    //{"success":true,"feedback":"Congratulations, you're right!"}
    //{"success":false,"feedback":"Wrong answer! Please, try again."}

    private boolean success;
    private String feedback;

    public Judge() {}

    public Judge(boolean success, String feedback) {
        this.success = success;
        this.feedback = feedback;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

class FormatDateTime {

    private static final String DATE_FORMATTER= "yyyy-MM-ddTHH:mm:ss.SSSSSS";
    private static String formatDateTime;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

    FormatDateTime(LocalDateTime date) {
        formatDateTime = date.format(formatter);
    }

    String getFormatDateTime() {
        return formatDateTime;
    }

    static LocalDateTime toLocalDateTime(String formatDate) {
        return LocalDateTime.parse(formatDate, formatter);
    }
}
