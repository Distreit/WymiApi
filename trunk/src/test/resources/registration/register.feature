Feature: Register

  Scenario: Register for a new account
    Given the input fields are entered as follows
      | name     | email                 | confirmEmail          | password | confirmPassword |
      | daveTest | niprat+test@gmail.com | niprat+test@gmail.com | test11TT | test11TT        |
      | daveTest | niprat+test@gmail.com | niprat+test@gmail.com | test11TT | test11TT        |
      | daveTest | niprat+test@gmail.com | niprat+test@gmail.com | test11TT | test11TT        |
      | daveTest | niprat+test@gmail.com | niprat+test@gmail.com | test11TT | test11TT        |
      | daveTest | niprat+test@gmail.com | niprat+test@gmail.com | test11TT | test11TT        |
      | daveTest | niprat+test@gmail.com | niprat+test@gmail.com | test11TT | test11TT        |
      | daveTest | niprat+test@gmail.com | niprat+test@gmail.com | test11TT | test11TT        |


    When the user submits the form
    Then the new user should be found in the database