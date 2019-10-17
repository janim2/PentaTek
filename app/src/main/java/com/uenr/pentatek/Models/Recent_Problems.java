package com.uenr.pentatek.Models;

public class Recent_Problems {

    public String problem_id;
    public String problem_description;
    public String prize;
    public String status;


    public Recent_Problems(){

    }

    public Recent_Problems(String problem_id, String problem_description, String prize,
                           String status) {

        this.problem_id = problem_id;
        this.problem_description = problem_description;
        this.prize = prize;
        this.status = status;
    }


    public String getProblem_id(){return problem_id; }

    public String getProblem_description(){return problem_description; }

    public String getPrize(){return prize; }

    public String getStatus(){return status; }


}
