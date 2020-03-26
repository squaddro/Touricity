package com.squadro.touricity.message.types.interfaces;

public interface IEntry extends IDataType {
    int getDuration();
    int getExpense();
    String getComment();
    int getIndex();
    String getEntry_id();

    void setDuration(int duration);
    void setExpense(int expense);
    void setComment(String comment);
    void setIndex(int index);
}
