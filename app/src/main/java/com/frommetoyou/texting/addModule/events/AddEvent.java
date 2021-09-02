package com.frommetoyou.texting.addModule.events;

public class AddEvent {
    public static final int SEND_REQUEST_SUCCESS = 0;
    public static final int ERROR_SERVER = 100;
    public static final int ERROR_DOESNT_EXISTS = 101;

    private int typeEvent;
    private int resMessage;

    public AddEvent() {
    }

    public int getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(int typeEvent) {
        this.typeEvent = typeEvent;
    }

    public int getResMessage() {
        return resMessage;
    }

    public void setResMessage(int resMessage) {
        this.resMessage = resMessage;
    }
}
