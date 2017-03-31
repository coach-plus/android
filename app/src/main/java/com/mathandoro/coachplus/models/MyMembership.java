package com.mathandoro.coachplus.models;

/**
 * Created by dominik on 31.03.17.
 */

public class MyMembership {
        protected String role;
        protected Team team;
        protected String user;

        public MyMembership(String role, Team team, String user) {
                this.role = role;
                this.team = team;
                this.user = user;
        }

        public String getRole() {
                return role;
        }

        public void setRole(String role) {
                this.role = role;
        }

        public Team getTeam() {
                return team;
        }

        public void setTeam(Team team) {
                this.team = team;
        }

        public String getUser() {
                return user;
        }

        public void setUser(String user) {
                this.user = user;
        }
}
