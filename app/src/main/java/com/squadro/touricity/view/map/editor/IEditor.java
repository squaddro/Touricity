package com.squadro.touricity.view.map.editor;

import com.google.android.gms.maps.GoogleMap;
import com.squadro.touricity.message.types.interfaces.IDataType;
import com.squadro.touricity.view.map.event.IDataUpdateListener;

public interface IEditor<T extends IDataType> {

    void prepare(GoogleMap map, T data);
    void dispose();

    void setDataUpdateListener(IDataUpdateListener<T> dataUpdateListener);
}
