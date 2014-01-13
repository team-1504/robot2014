/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team1504.robot2014;

/**
 *
 * @author Gavaga
 */
public class Utils 
{
    public static String[] split(String str, char split)
    {
        String copy = str;
        int piece_counter = 0;
        while (str.indexOf(copy, piece_counter) != -1)
        {
            ++piece_counter;
        }
        String[] split_string = new String[piece_counter];
        int first_index = 0;
        int second_index = 0;
        for (int i = 0; i < piece_counter; ++i)
        {
            second_index = copy.indexOf(first_index, split);
            split_string[i] = copy.substring(first_index, copy.indexOf(first_index, split));
            first_index = second_index + 1;
        }
        return split_string;
    }
    
}
