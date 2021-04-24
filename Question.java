import java.util.Arrays;
/**
 * @author Robin Warren
 * A Question is POJO representing a Bar Exam question. A Question object will consist of
 * a question number; the question text and the 4 answers as strings.
 */

public class Question {
    private Integer questionNumber;
    private String questionText;
    private String[] answerTexts;

    public Question(Integer questionNumber, String questionText){
        this.questionNumber = questionNumber;
        setQuestionText(questionText);
        this.answerTexts = setAnswerTexts();
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    /**
     * questionText is the question text from all the text in a question (Q+As). This method just
     * returns the text between the question number and the first answer
     * @return the question paragraph and the actual question.
     */
    public String getQuestionText() {
        String a = " (A)";
        int indexOfPoint = questionText.indexOf(".") + 1;
        //questionText.replaceAll("\t", "");
        //Each questions starts with a number, full stop then a space //TODO This will need to be changed for No Q's above 100
        return questionText.substring(indexOfPoint, questionText.indexOf(a));
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

//    public String trimQuestionNumber(String questionText){
//        //Each questions starts with a number, full stop then a space //TODO This will need to be changed for No Q's above 100
//        return this.questionText.substring(3).trim();
//    }

    /**
     * This method uses the full questionText string and creates strings of the 4 answers.
      * @return Array with the 4 answers as strings.
     */
    public String[] setAnswerTexts(){
        String[] answers = new String[4];
        String a = " (A)";
        String b = " (B)";
        String c = " (C)";
        String d = " (D)";
        String answer_A = this.questionText.substring(questionText.indexOf(a) , questionText.indexOf(b));
        String answer_B = this.questionText.substring(questionText.indexOf(b), questionText.indexOf(c));
        String answer_C = this.questionText.substring(questionText.indexOf(c), questionText.indexOf(d));
        String answer_D = this.questionText.substring(questionText.indexOf(d));
        answers[0] = answer_A;
        answers[1] = answer_B;
        answers[2] = answer_C;
        answers[3] = answer_D;
        return answers;
    }
    public String[] getAnswerTexts(){
        return this.answerTexts;
    }

    @Override
    public String toString() {
        return  "Question number: " + questionNumber +
                "\nQuestion Text:  '" + questionText +
                "\nAnswers: " + Arrays.toString(answerTexts);
    }
}

