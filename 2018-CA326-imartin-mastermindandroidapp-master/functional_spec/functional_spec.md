---
title: Contents
---

1.  Introduction

    1.  Overview

    2.  Business Context

    3.  Glossary

2.  General Description

    1.  Product/System Functions

    2.  User Characteristics and Objectives

    3.  Operational Scenarios

    4.  Constraints

3.  Functional Requirements

    1.  User Login

    2.  Exit App

    3.  Find Game

    4.  Challenge Friend

    5.  Play Game

    6.  Rematch

    7.  Quit to Main Menu

4.  System Architecture

    1.  Diagram

5.  High Level Diagrams

    1.  High Level Diagram

6.  Preliminary Schedule

    1.  Overview

    2.  Task list

    3.  GANTT Chart

7.  Appendices

    1.  References and Resources

1. Introduction
===============

1a.Overview

The product is an Android application which allows users to play the code
breaker game mastermind against each other over a network. Players will sign in
using their google play accounts and they will be able to find matches with
random people over the network or play against their friends. The product will
make use of the existing google play framework and database to enable the
networking aspect of the game.

Players will play for 3 rounds in each game. There will be a default of 10 tries
to figure out the code and a 4 minute time limit per round. Settings such as
time limit and number of tries will be customizable.

Players will have the option for a rematch or quit to main menu after the game
is complete. There will be a chat box on screen during the game for both players
to communicate while playing.

The Android application is being developed because there is a need in the
marketplace for a head-to-head game which requires logical thinking and
problem-solving. Mastermind is an excellent game to encourage the development of
a person’s problem solving skills, a set of skills that are crucial not just in
the world of computing, but in any walk of life.

1b.Business Context

There is only one viable business context in relation to this product and that
is advertisement.

The product could feature advertisements as a way have generating money for the
application. These advertisements could come from any businesses or
organizations looking to promote their own products or services. The
advertisements could be a small banner within the game that would not hinder the
user’s experience.

1c.Glossary

**Mastermind:**

Mastermind is a classic board game where 2 players compete against each other.
One player sets a coloured code sequence and their opponent must try to guess
the code in the allotted amount of guesses. The code breaker is given feedback
on their attempts and from this they must use deduction to break the code.  

**Android:**

Android is a mobile operating system developed by Google, based on the Linux
kernel and designed primarily for touchscreen mobile devices such as smartphones
and tablets.

**Google Play Services:**

Google Play Services is a proprietary background service and API package for
Android devices.

2. General Description
======================

2a.Product/System Functions

Here is a list of the main functions in our project:

-   User Login

-   Exit App

-   Find Game

-   Challenge Friend

-   Accept Challenges

-   Change Settings(Game Settings)

-   Play Game

-   Request Rematch

-   Quit to main menu

2b.User Characteristics and Objectives

There is no defined target audience for this application, in general, just
people that have some time to kill and are looking for a quick and fun way to
test their problem solving abilities.

Most people that would use this application will have had some experience with
similar games before, as it will follow a fairly standard design pattern of
mobile app games.

The objective of the mobile app is to be fun and easy for users of all technical
abilities to use, whilst honing the user’s logical thinking and problem-solving
skills.

The system must be fully functional with no major bugs, run at a tolerable speed
and look professional on most android devices. There can be no noticeable delay
between the devices.

2c.Operational Scenarios

**Player searches for a game:**

When a player searches for a game the app looks for another play that is looking
for a game at the same time. The app matches two players and sends them both to
the game setup screen.

**Player challenges a friend:**

If the player wishes to challenge a friend to a game then all they need to do is
enter the Google Play user name of their friend. A notification is sent to the
friend and upon the acceptance of the challenge both players are sent to the
game setup screen.

**Setting up a game:**

One player is selected as the host at random and this player is in control of
the settings of the game. Both players can use the chat box at this time to
agree which settings should be used. Settings like time limit, number of tries
and whether duplicates are allowed will all be customisable. Both ready up once
settings are agreed upon.

**Start of game:**

Once both players are readied up player 1 is sent to the game board screen and
asked to select his/her code sequence. While player 1 is selecting their code
player 2 is sent waiting screen which prompts a message telling them that player
1 is currently selecting the code. Once player 1 confirms their code both
players are sent to the game board screen and player 1 watches as player 2
attempts to break the code. For round 2 the process is the same but the roles of
the players are reversed.

**Final round of game:**

In this round both players are sent straight to the game board screen.For this
round the code that is to be cracked will be generated by the app. Both players
will then attempt to crack the code on their own boards which will not be
visible to the other player. As soon a one player figures out the code correctly
the round is finished, the other player will be notified that the code has been
cracked and the round ends. The points for this round go to the player that
cracks the code first. In the event that both players run out of time or tries
then neither player receives any points.

**Try with correct colour in wrong position:**

If a player tries a sequence with a correct colour but in the wrong position the
app will notify them with a white peg beside their try. The number of white pegs
indicates the number of correct colours that the player has in there try that
are not in the correct position. The feedback from the white peg gives no
indication as to which colour is refers to.

**Try with correct colour in correct position:**

If a player tries a sequence with a correct colour in the correct position the
app will notify them with a red peg beside their try. The number of red pegs
indicates the number of colours that the player has put into the correct
position. The feedback from the red pegs gives no indication as to which colour
it refers to.

**Try which cracks the code:**

If a player tries a sequence with all the colours in their correct positions
then the code has been cracked, the game shows a prompt to both stating that the
code has been broken and that the player that entered the try has one the round.
The game either starts the next round or if it is the final round, displays an
end game screen.

**Player runs out of tries or time:**

If a player uses too many tries or takes too long on their go the game will
display a prompt to both players stating that the round has been won. The game
either starts the next round or if it is the final round, displays an end game
screen.

**End game screen:**

This is the screen that both players are brought to at the end of the game. This
screen will display the total time of the game, the points of both players and
the name of the victor. Players will be given the option on this screen to have
a rematch or to quit to the main menu. If both players select rematch then both
players are brought to the game setup screen. If one player picks quit to menu
then both players are sent to the main menu.

2d.Constraints

**Time constraints:**

The project is due on the 9th of March in semester 2 which is not a lot of time
to complete a project with as much features as we are trying to implement. This
will mean time management and sticking to deadlines will be paramount.

**Knowledge of Android:**

Our knowledge of Android and the platforms used to develop the application is
quite limited, which may prove challenging to learn quickly.

**Device speed:**

There are a whole range of Android devices on the market so our application will
have to be optimised for these devices.

3. Functional Requirements
==========================

3a.User Login

*Description:*

Users must be able to login to their accounts before being able to play the
game. The user will either be signed in on their device already, in which case,
no further action is required by the user. If the user is not signed in to their
account they will be prompted to sign in before the app loads.

*Criticality:*

This function is essential to the system as without it users would not be able
to find games or challenge friends on the app. With a successful login users
will be able to avail of all the services like challenging and adding friends.

*Technical Issues:*

The main issue here is making sure that the username and password that is
entered is a valid combination and linked to a real account.

*Dependencies:*

None

3b.Exit App

*Description:*

Users must be able to quickly exit the app at any time during the duration of
the app being used. This includes situations like in the main menu or in the
game.

*Criticality:*

This function is essential to the system as if this function was not on the app
then the users would almost feel trapped on the system with the only way to
leave the app being to manually close the app.

*Technical Issues:*

The app will have to make sure that no connections with the server remain after
the app after the user has exited the app. If the user exits during a game their
opponent must be notified of the exit.

*Dependencies:*

None

3c.Find Game

*Description:*

Users must be able to search for a game against another user that is searching
for a game at that time.

*Criticality:*

This function is essential to the system as if this function was not there
players would not be able to actually use the apps function which is playing a
game of Mastermind.

*Technical Issues:*

The main issue here is getting both players connected to each other after the
game has been found. The connection has to be good enough for fluid gameplay
between the two players.

 

*Dependencies:*

This requirement is dependent on the user login because without this the
matchmaking services wouldn’t be accessible to the user.

3d.Challenge Friend

*Description:*

Users must be able to challenge a friend to a match through the app. Users must
be able to accept the challenge and both users must be brought to the game setup
screen once the challenge has been accepted.

*Criticality:*

While this function is not essential to the app it is something that has become
common throughout apps on all devices. Without this function users would feel
like a function is missing that could greatly improve the app and would not be
happy with only being able to play strangers.

*Technical Issues:*

The main issue here is making sure that both players join the same server for
their game and that a challenge cannot be accepted if one of the users in no
longer online on the app.

*Dependencies:*

This requirement is dependent on the user login because without this the player
being signed in the users wouldn’t be able to add friends.

3e.Change Settings

*Description:*

Users must be able to change the game settings like time limit, number of tries
and if duplicate colours are allowed.

*Criticality:*

While this function is not essential to the app it is something that will
greatly add to the app and will give users a greater sense of involvement in the
game process as a whole.

*Technical Issues:*

The main issue here will be making sure that the changes like number of tries
doesn’t break the user interface of the code if it is increased. The settings
must also be made universal between both players.

*Dependencies:*

This requirement is dependent on the user login because without this the Google
Play services wouldn’t be accessible to the user.

3f.Play Game

*Description:*

Users must be able to play a full game of Mastermind, this includes submitting a
sequence, giving correct feedback, generating the sequence for the third round
and ending when conditions are met.

*Criticality:*

This function is arguably the most important function as it is the purpose of
the app, all other functions surrounding this are there to make the experience
of playing the game more enjoyable.

*Technical Issues:*

The main issues here will be the visualisation of the game itself, making sure
that it updates for both players simultaneously and making sure that both
players end the game at the same time.

*Dependencies:*

This is dependent on either the find game or challenge friend functions as
without at least one of them users wouldn’t be able to play a game.

3g.Rematch

*Description:*

Users must be able to rematch each other once the game finishes. This requires
players being able to send and accept rematch requests.  

*Criticality:*

While this function is not essential to the app it is something that will
greatly add to the app as it will mean if users want to play a stranger again
they can and if they want to play a friend again they don’t have to go through
the challenge menu each time.

*Technical Issues:*

The main issue here will be making sure that if only one person wants a rematch
that person is notified that the other person has left the room. This ensures
users aren’t left to decide for themselves if an opponent has left or not.

*Dependencies:*

This function is dependent on the play game function as the rematch option will
be on the end game screen, this screen will not be available if a game has not
been played.

3h.Quit to Main Menu

*Description:*

Users must be able to quit to the main menu during setup of the game, at any
stage of the game or in the end game screen.

*Criticality:*

This function is essential to the app as without it users would be stuck in the
game once they have found a game with the only way to leave being to restart the
app. This would be a massive inconvenience which could make a large number of
users stop using the app.

*Technical Issues:*

The main issue here will be similar to exiting the app. If the user quits to the
menu their opponent must be notified quickly so to avoid the opponent waiting in
a pointless game.

*Dependencies:*

None

4. System Architecture 
=======================

![https://lh3.googleusercontent.com/jGBYbQQfdZLULNgTuGM5wKd00MqjIN-G2mpaSriAyRDjKdn84smFtXRl-jMidW88UFpwiB6WHZpmLemTM32sTs9Q5w6tnzpNBpd6-t5p5R4kUD2PGe1wD5IzmvLdzbdcwoRWgYOH](media/65761700a31ed621ecfbe3bffddd81ed.png)

**Figure A**

5. High Level Design
====================

5a.High Level Design Diagram

![https://lh4.googleusercontent.com/kpLOIVpqjXK58hC8y_j-ndekW0BvPCHVRy2qqy84B3Kq0xD727XksnBGf7HCMes7L5NnFkpt8TUJ0JaWJbRZJh-PJ8B36ZDMYkz1FrS83DFc-1rUc9o6CI7w-vo84Tg0BOXkeus-](media/d2639905bc63d7e9969b2cf52e081a4d.png)

**Figure B**

5b. High Level Design Descriptions

**Descriptions for figure B:**

-   **Log In**

**         **Log into your account using username and password.

-   **Matchmaking**

**        ** Search for a game and connect both users into a lobby.

-   **Game Setup**

**         **Allow users to agree upon the game settings such as time limit.

-   **Start Game**

**         **Initialise the game and bring both players to the game screen.

-   **End Game**

**         **As the game is completed, bring both players to the end game
screen.

-   **Rematch/Exit            **

**       **Give both players the choice to have a rematch or exit to the main
menu.

6. Preliminary Schedule
=======================

6a.Overview of schedule

In this section is an early estimate at the timeline that our project will use
while we complete it. This timeline includes the tentative start and stop dates,
task interdependence and major task to be completed. This timeline is
represented using a task list (**Fig C**) and GANTT chart (**Fig D**). The
software needed for this project will include Google Play Services, Android and
Eclipse for java/ Android Studio.

6b.Task list

![https://lh6.googleusercontent.com/0u-aHpQ1ENXjO5F_7vvXkqhQYYhtUEvQVy7EUSLFyZy5ghe6hzdKNNdxlCu5U7-qDX57vu_i9UivB2s_oeiQCX2BsJFS-9K0FvhAr0Xs5lRmomLJe-2w90ZcXyoPT8b-ulaIh3B9](media/64af24f005dec342ac61df1765402c85.png)

**Fig C**

6c.GANTT chart

![https://lh4.googleusercontent.com/sa9plnvbUwi1qX6QJ7EbSFVrk-NbLuI9cQg7eMfzyS5ZOC5mbfdofBG3KuGR66OAPEvG5rCTFnalV7WVMrQpPAYHr8EqaKq0qjCLp_OH69w9AIeTHwo5PrYwVvOtvKtMwGQq9Tbv](media/5f38b16f0e1128d9902ff1a734045304.png)

**Fig D**

7. Appendices
=============

*Google Play Services:*

<https://developers.google.com/games/services/android/realtimeMultiplayer>

*Eclipse for Java:*

<https://www.eclipse.org/>

*Mastermind Rules:*

<http://www.boardgamecapital.com/mastermind-rules.htm>
