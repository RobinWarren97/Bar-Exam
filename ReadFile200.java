import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Robin Warren
 * This class is used to read in the question and answers text file. The text is processed,
 * and a JSON file is created. JSON file contains the 4 pairs of Q/A and boolean correct.
 * Specfically this class is used for older NCBE Question.
 *
 * This is a copy of the ReadFile class as the  caveats/nuances of the files require a lot
 * of changes - but similar structure.
 */
public class ReadFile200 {
        public static void main(String args[]) {
            //reading file line by line in Java using BufferedReader
            FileInputStream fis = null;
            BufferedReader reader = null;
            //Arraylists to store the objects
            ArrayList<Question> listOfQuestions = new ArrayList<>();
            ArrayList<Answer> answerList = new ArrayList<>();
            ArrayList<StringBuilder> sbs = new ArrayList<>();


            try{
                //Read in answers set as object use basic File obj and Scanner - doesn't need to be a stream.
                File myObj = new File("Feb-200-Ans.txt");
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    String[] splitLine = data.split(" ");
                    String answerNum = splitLine[0];
                    String actualAns = splitLine[1];
                    Answer a = new Answer(Integer.parseInt(answerNum), actualAns);
                    answerList.add(a);
                }
            }catch (FileNotFoundException ex){
                Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
            }

            //An inital count number is used to read in Question 1 and reads until question 2
            //initCount is incremented according to the number of questions in a given file
            int initCount = 2;
            try {
                fis = new FileInputStream("Feb-200.txt");
                reader = new BufferedReader(new InputStreamReader(fis));
                String line = reader.readLine();
                //Read in file until end line
                while(line != null){
                    StringBuilder sb = new StringBuilder();
                    //Some of the questions are in a different layout - Two questions rely on one question paragraph
                    while (!line.contains(initCount + ".")){
                        if(line.contains("Questions " + initCount)){
                            sbs.add(sb);
                            String[] lineSplit = line.split(" ");
                            String questionNumbers = lineSplit[1];
                            String[] nums = questionNumbers.split("-");
                            int lowerNumber = Integer.valueOf(nums[0]);
                            int higherNumber = Integer.valueOf(nums[1]);
                            StringBuilder doubleQuestionText = new StringBuilder();
                            while (!line.contains(initCount + ".")){
                                doubleQuestionText.append(line + " ");
                                line = reader.readLine();
                            }

                            initCount++;
                            StringBuilder sbDoubleQuestions = new StringBuilder();

                            while (!line.contains((higherNumber+1) + ".")){
                                sbDoubleQuestions.append(line + " ");
                                line = reader.readLine();
                            }
                            int doubleQuestionLoop = (higherNumber - lowerNumber);
                            initCount = higherNumber+1;
                            ArrayList<StringBuilder> listOfSBs = doubleQuestionToSBs(doubleQuestionText, sbDoubleQuestions,
                                                                                                    lowerNumber, higherNumber);
                            for (StringBuilder stringBuilder : listOfSBs){
                                sbs.add(stringBuilder);
                            }

                        } else {
                            //System.out.println(line);
                            sb.append(line + " ");
                            line = reader.readLine();
                        }
                    }
                    sbs.add(sb);
                    initCount++;
                    //This specific file has 200Qs so count will end at 11.
                    if(initCount == 201){
                        break;
                    }
                }
                for (StringBuilder sb: sbs) {
                    //Get question numbers upto 3 digits
                    int nums = sb.indexOf(".");
                    String trimed = sb.substring(0, nums);
                    Question q = new Question(Integer.valueOf(trimed), sb.toString());
                    System.out.println(q.toString());
                    listOfQuestions.add(q);

                }
                fis.close();
                reader.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Create a Feature Obj for each question and answer pair.
            ArrayList<Feature> features = new ArrayList<>();
            for (Question q : listOfQuestions){
                String questionNumber = q.getQuestionNumber().toString();
                String questionText = q.getQuestionText();
                String[] answersText = q.getAnswerTexts();

                String answerString = "";
                for (Answer a : answerList){
                    if (a.getAnswerNumber() == q.getQuestionNumber()){
                        answerString = a.getAnswer();
                    }
                }

                //Depending on the correct answer (A,B,C,D). Create a Feature for each answer, setting the correct ans
                //as correct and the others as false.
                String temp = "(" + answerString + ")";
                String actualAnswer = "";
                for (String s: answersText){
                    if (s.contains(temp)){
                        actualAnswer = s;
                        String fullFeature = questionText + actualAnswer;
                        Feature f = new Feature(fullFeature.replaceAll("\\t+", ""), true);
                        features.add(f);
                    } else {
                        String fullFeature = questionText + s;
                        Feature f = new Feature(fullFeature.replaceAll("\\t+", ""), false);
                        features.add(f);
                    }
                }
            }
            //Using a JSON Lib to convert POJO to JSON
            for (Feature feature : features){
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = null;
                try {
                    json = ow.writeValueAsString(feature);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                System.out.println(json);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("./Feb-Correct.json"), features);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //System.out.println(feature.toString());
            }
        }

    /**
     * Some questions in the files are in a different format. E.g. Two answers relate to 1 question. This method handles
     * this.
     * @param doubleQuestionText
     * @param sbDoubleQuestions
     * @param lowerNumber
     * @param higherNumber
     * @return An ArrayList of Stringbuilders to be added to the overall questions reading in
     */
        public static ArrayList<StringBuilder> doubleQuestionToSBs(StringBuilder doubleQuestionText, StringBuilder sbDoubleQuestions,
                                                                   int lowerNumber, int higherNumber){
            ArrayList<StringBuilder> listOfsbs = new ArrayList<>();
            //Having to hard code number of questions in the special cases
            //Case that the question is for the next 2 questions.
            if((higherNumber - lowerNumber) == 1){
                int index = sbDoubleQuestions.indexOf(String.valueOf(higherNumber));
                String firstQuestion = sbDoubleQuestions.substring(0, index);
                String secondQuestion = sbDoubleQuestions.substring(index, sbDoubleQuestions.length());
                StringBuilder firstSB = new StringBuilder();
                StringBuilder secondSB = new StringBuilder();
                firstSB.append(lowerNumber + ". " + doubleQuestionText.substring(17) + " " + firstQuestion.substring(4));
                secondSB.append(higherNumber + ". " + doubleQuestionText.substring(17) + " " + secondQuestion.substring(4));
                listOfsbs.add(firstSB);
                listOfsbs.add(secondSB);
            } else if ((higherNumber - lowerNumber) == 2){
                int indexOfSecond = sbDoubleQuestions.indexOf(String.valueOf(lowerNumber + 1));
                int indexOfThird = sbDoubleQuestions.indexOf(String.valueOf(higherNumber));
                String firstQuestion = sbDoubleQuestions.substring(0, indexOfSecond);
                String secondQuestion = sbDoubleQuestions.substring(indexOfSecond, indexOfThird);
                String thirdQuestion = sbDoubleQuestions.substring(indexOfThird, sbDoubleQuestions.length());
                StringBuilder firstSB = new StringBuilder();
                StringBuilder secondSB = new StringBuilder();
                StringBuilder thirdSB = new StringBuilder();
                firstSB.append(lowerNumber + ". " + doubleQuestionText.substring(17) + " " + firstQuestion.substring(4));
                secondSB.append((lowerNumber + 1) + ". " + doubleQuestionText.substring(17) + " " + secondQuestion.substring(4));
                thirdSB.append(higherNumber + ". " + doubleQuestionText.substring(17) + " " + thirdQuestion.substring(4));
                listOfsbs.add(firstSB);
                listOfsbs.add(secondSB);
                listOfsbs.add(thirdSB);
            } else {
                System.out.println("Question number not found");
            }
            return listOfsbs;
        }
}
