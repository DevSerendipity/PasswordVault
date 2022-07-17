package org.foolProof.PasswordVault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication @RestController @ServletComponentScan public class DemoApplication {

    // todo implement one of this sadly haven't found a way to get the parent directory of a selected file
    //  only way I think this would work is that webkitdirectory way and tell the user to make an specific folder for it
    //or two make the user select a directory, do nothing but store the relative path than user selects the file
    public static void main( String[] args ) {
        SpringApplication.run( DemoApplication.class, args );
    }
    // if i chose to select a directory and encrypt everything in it, than i would need to work on the ability to
    // being able to send multiple files and than multiple decryption
    // making it seperate to select a directory than to select a file could result in user selecting 2 unrelated
    // things which would result in file missing
}
