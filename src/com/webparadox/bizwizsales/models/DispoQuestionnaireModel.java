package com.webparadox.bizwizsales.models;

import java.util.ArrayList;

import android.util.Log;

public class DispoQuestionnaireModel {
	String Question = "";
	Integer QuestionId=0;
	String QuestionType = "";
	String ResponseId = "0";
	String ResponseText = "";
	String ResponseChoiceId="";
	public ArrayList<DispoChoiceOptions> dispoChoiceArray = new ArrayList<DispoChoiceOptions>();
	public String getQuestion() {
		return Question;
	}
	public void setQuestion(String question) {
		Question = question;
	}
	public Integer getQuestionId() {
		return QuestionId;
	}
	public void setQuestionId(Integer questionId) {
		Log.d("questionId", questionId+"");
		QuestionId = questionId;
	}
	public String getQuestionType() {
		return QuestionType;
	}
	public void setQuestionType(String questionType) {
		QuestionType = questionType;
	}
	public String getResponseId() {
		return ResponseId;
	}
	public void setResponseId(String responseId) {
		ResponseId = responseId;
	}
	public String getResponseText() {
		return ResponseText;
	}
	public void setResponseText(String responseText) {
		ResponseText = responseText;
	}
	public String getResponseChoiceId() {
		return ResponseChoiceId;
	}
	public void setResponseChoiceId(String responseChoiceId) {
		
		ResponseChoiceId = responseChoiceId;
	}
	public ArrayList<DispoChoiceOptions> getDispoChoiceArray() {
		return dispoChoiceArray;
	}
	public void setDispoChoiceArray(ArrayList<DispoChoiceOptions> dispoChoiceArray) {
		this.dispoChoiceArray = dispoChoiceArray;
	}
	@Override
	public boolean equals(Object o) {
		boolean equal = false;
		if (o instanceof DispoQuestionnaireModel) {
			DispoQuestionnaireModel question = (DispoQuestionnaireModel) o;
			equal = (question.getQuestionId() == this.getQuestionId()) ? true : false;
		}
		return equal;

	}


}
