/**
 * @author Robin Warren
 * A 'Answer' is POJO representing an Answer string and question number from the
 * given answer list to a past Bar Exam paper.
 */
public class Answer {
    private int answerNumber;
    private String answer;

    public Answer(int answerNumber, String answer) {
        this.answerNumber = answerNumber;
        this.answer = answer;
    }

    public int getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(int answerNumber) {
        this.answerNumber = answerNumber;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "answerNumber=" + answerNumber +
                ", answer='" + answer + '\'' +
                '}';
    }
}
