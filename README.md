# Project Name
2-Player Minesweeper

**Status:** Archived. Built for Advanced CS Topics/Projects [2024-2025]. Worked at submission; dependencies may be outdated.

## About
Created a 2-player minesweeper game using Java's graphics interface.

How the game works:
- each player has a 20-second clock
- each player takes turns uncovering (left click) or flagging (right click) one square on the board
- each square uncovered or flagged nets the player who uncovered it 2 extra seconds
- when a non-mine square is uncovered, it shows how many mines are adjacent to it
- when a mine is uncovered, it costs that player an increasing number of points (-1 for first mine, -2 for second mine, etc.)
- when a mine is flagged, it nets the player one point
- game ends when every mine is flagged or when a player runs out of time
- if the former happens, player with more points wins!
- if the latter happens, player with more time wins!

## Built With
Java, Graphics library
