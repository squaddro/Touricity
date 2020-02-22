package com.squadro.touricity.view.map.event;

import com.squadro.touricity.message.types.interfaces.IDataType;

public interface IDataUpdateListener <T extends IDataType> {
    void onDataUpdate(T data);
}
