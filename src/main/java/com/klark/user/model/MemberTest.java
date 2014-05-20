// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.user.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description here!
 * 
 * 
 * @author
 */

public class MemberTest {

    Map<Integer, Set<Member>> map = new HashMap<Integer, Set<Member>>();
    int i = 1;

    void printSocialGraph(Member m) {

        if (m.getFriends() == null) {
            System.out.println(map);
            return;
        }

        Set<Member> fLevel = new HashSet<Member>();
        for (Member m1 : m.getFriends()) {
            fLevel.add(m1);
            if (m1.getFriends() != null) {
                printSocialGraph(m);
            }

        }
        map.put(i, fLevel);
        i++;

    }

    public static void main(String s[]) {

        MemberTest m = new MemberTest();

        Member m1 = new Member();
        m1.setEmail("das@f.com");
        m1.setName("Das");

        Member f1 = new Member();
        f1.setEmail("ravi@f.com");
        f1.setName("ravi");
        List<Member> fList = new ArrayList<Member>();
        List<Member> f2List = new ArrayList<Member>();

        Member f11 = new Member();
        f11.setEmail("satish@f.com");
        f11.setName("satish");

        Member f2 = new Member();
        f2.setEmail("ram@f.com");
        f2.setName("ram");
        f2.setFriends(f2List);

        f2List.add(f2);

        f1.setFriends(f2List);

        fList.add(f1);
        fList.add(f11);
        m1.setFriends(fList);

        m.printSocialGraph(m1);

    }

    static class Member {
        String name;
        String email;
        List<Member> friends;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<Member> getFriends() {
            return friends;
        }

        public void setFriends(List<Member> friends) {
            this.friends = friends;
        }

    }
}