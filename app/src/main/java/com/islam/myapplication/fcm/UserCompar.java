package com.islam.myapplication.fcm;

import com.islam.myapplication.fcm.model.User;

import java.util.Comparator;

public class UserCompar  implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return o2.getFirst()-o1.getFirst();
    }

}
