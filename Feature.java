/**
 * @author Robin Warren
 * A 'Feature' is POJO representing a 'data point', for the RNN in the project. A feature is just a
 * string which is comprised of the question text and the answer text (A || B || C || D).
 * Followed by a boolean to indicate if the answer is correct for the question.
 * */

public class Feature {
    private String feature;
    private Boolean isCorrect;

    public Feature(String feature, Boolean isCorrect) {
        this.feature = feature;
        this.isCorrect = isCorrect;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "feature='" + feature + '\'' +
                ", isCorrect=" + isCorrect +
                '}';
    }
}
