/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainfunctions;

import Lib.PasswordEncoding;
import Lib.ProsedeurControls;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Massa
 */
public class UserController {

    public String isValidLogin(String username, String password) {
        String msg = "error";
        int blockcount = 0;
        ProsedeurControls pc = new ProsedeurControls();
        PasswordEncoding pe = new PasswordEncoding();
        try {
            String encpwd = pe.Encode(password);
            String para = "('" + username + "')";
            ResultSet res = pc.callProc("selectuser", para);
            if (res.next()) {
                blockcount = res.getInt("counter");
              //  System.out.println("bc = " + blockcount);
                para = "('" + username + "','" + encpwd + "')";
                res = pc.callProc("logincontrol", para);
                if (res.next()) {
                    String state = res.getString("state");
                    String usertype = res.getString("usertype");
                    int counter = res.getInt("counter");
                    if (!state.equals("active")) {
                        msg = "You are deactive. Tell to the admin.";
                    } else {
                        if (counter >= 5) {
                            msg = "you are blocked due to incorrect password.";
                        } else {
                            para = "('" + username + "')";
                            pc.callProc("validpassword", para);
                            msg = usertype;
                        }
                    }
                } else {
                    if (blockcount >= 5) {
                        msg = "you are blocked due to incorrect password.";
                    } else {
                        msg = "Password is not correct";
                    }

                    para = "('" + username + "')";
                    pc.callProc("invalidpassword", para);
                }
            } else {
                if (blockcount >= 5) {
                    msg = "You enter incorect password more than 5 times. you are blocked.";
                } else {
                    msg = "User ID is not correct";
                }

            }

        } catch (Exception ex) {

            msg = "User ID is not correct";

            String para = "('" + username + "')";
            pc.callProc("invalidpassword", para);
            // Logger.getLogger(loginclass.class.getName()).log(Level.SEVERE, null, ex);
        }

        return msg;
    }

  /*  public static void main(String[] args) {
        UserController lc = new UserController();
        String word = lc.isValidLogin("doc1", "doc1");
        System.out.println(word);
    }*/
}
