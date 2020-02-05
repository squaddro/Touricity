package com.squadro.touricity.view.routeList.event;

import com.squadro.touricity.message.types.AbstractEntry;

public interface IEntryEventListener extends IEntryMoveEventListener, IEntryRemoveEventListener {
    void onClickEntry(AbstractEntry entry);
    void onHoldEntry(AbstractEntry entry);
}
