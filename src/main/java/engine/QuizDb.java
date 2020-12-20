package engine;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class QuizDb {
/*
{
  "id": 1,
  "title": "Coffee drinks",
  "text": "Select only coffee drinks.",
  "options": ["Americano","Tea","Cappuccino","Sprite"],
  "answer": [0,2]
}
*/
    @Id
    private long id;
    private String title;
    private String text;
    private String options;
    private String answer;
    private String owner;

    public QuizDb() {}

    public QuizDb(long id, String title, String text, String options, String answer, String owner) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.options = options;
        this.answer = answer;
        this.owner = owner;
    } 

    public QuizDb(long id, String title, String text, String[] options, int[] answer, String owner) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.setOptions(options);
        this.setAnswer(answer);
        this.owner = owner;
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

    public String getOptions() {
        return options;
    }

    public String getAnswer() {
        return answer;
    }

    public String getOwner() {
        return owner;
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

    public void setOptions(String options) {
        this.options = options;
    }
   
    public void setAnswer(String answer) {
        this.answer = answer;
    }
  
    public void setOwner(String owner) {
        this.owner = owner;
    }

    void setOptions(String[] options) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(options[i]);
        }
        this.options = sb.toString();
    }

    void setAnswer(int[] answer) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < answer.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(answer[i]);
        }
        this.answer = sb.toString();
    }

    String[] getOptionsArray() {
        String[] strs = options.split(",");
        String[] array = new String[strs.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = strs[i];
        }
        return array;
    }
    
    int[] getAnswerArray() {
        if ("".equals(answer)) {
            return new int[0];
        }
        String[] strs = answer.split(",");
        int[] array = new int[strs.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(strs[i]);
        }
        return array;
    }
}
