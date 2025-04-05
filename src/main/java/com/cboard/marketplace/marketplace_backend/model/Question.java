package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.cboard.marketplace.marketplace_common.*;

//entity maps this saying this is a table


//@NoArgsConstructor
//@AllArgsConstructor
@Entity
public class Question
{
    //lombok auto does getters and setters? to redue code
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String rightAnswer;
    private String difficultyLevel;
    private String category;



    // 👇 **Manually add a No-Args Constructor**
    public Question() {}

    // 👇 **Manually add an All-Args Constructor**
    public Question(int id, String questionTitle, String option1, String option2, String option3, String option4,
                    String rightAnswer, String difficultyLevel, String category) {
        this.id = id;
        this.questionTitle = questionTitle;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.rightAnswer = rightAnswer;
        this.difficultyLevel = difficultyLevel;
        this.category = category;

    }

    public int getId() { return id; }
    public String getQuestionTitle() { return questionTitle; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }
    public String getRightAnswer() { return rightAnswer; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public String getCategory() { return category; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setQuestionTitle(String questionTitle) { this.questionTitle = questionTitle; }
    public void setOption1(String option1) { this.option1 = option1; }
    public void setOption2(String option2) { this.option2 = option2; }
    public void setOption3(String option3) { this.option3 = option3; }
    public void setOption4(String option4) { this.option4 = option4; }
    public void setRightAnswer(String rightAnswer) { this.rightAnswer = rightAnswer; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public void setCategory(String category) { this.category = category; }



}
