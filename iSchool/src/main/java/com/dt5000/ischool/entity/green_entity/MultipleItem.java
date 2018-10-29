package com.dt5000.ischool.entity.green_entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.dt5000.ischool.entity.PersonMessage;

/**
 * Created by weimy on 2017/12/27.
 */

public class MultipleItem implements MultiItemEntity {
    public static final int Group = 0x01;
    public static final int Sigle = 0x02;
    //type
    private int type;
    //sigle
    private PersonMessage personMessage;
    //group
    private GroupSendMessage groupSendMessage;

    public MultipleItem(int type, PersonMessage personMessage) {
        this.type = type;
        this.personMessage = personMessage;
    }

    public MultipleItem(int type, GroupSendMessage groupSendMessage) {
        this.type = type;
        this.groupSendMessage = groupSendMessage;
    }

    public PersonMessage getPersonMessage() {
        return personMessage;
    }

    public void setPersonMessage(PersonMessage personMessage) {
        this.personMessage = personMessage;
    }

    public GroupSendMessage getGroupSendMessage() {
        return groupSendMessage;
    }

    public void setGroupSendMessage(GroupSendMessage groupSendMessage) {
        this.groupSendMessage = groupSendMessage;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
