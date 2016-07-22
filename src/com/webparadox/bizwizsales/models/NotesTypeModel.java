package com.webparadox.bizwizsales.models;

public class NotesTypeModel {

	public String noteTypeId;
	public String noteType;
	public String tableId;

	
	public String getNoteTypeId() {
		return noteTypeId;
	}


	public void setNoteTypeId(String noteTypeId) {
		this.noteTypeId = noteTypeId;
	}


	public String getNoteType() {
		return noteType;
	}


	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}


	public String getTableId() {
		return tableId;
	}


	public void setTableId(String tableId) {
		this.tableId = tableId;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return noteType;
	}
}
