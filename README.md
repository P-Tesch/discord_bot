# Discord Bot

Discord bot made using [Java Discord API (JDA)](https://github.com/DV8FromTheWorld/JDA) for use in my own personal discord server and for learning purposes.

## Functions

For a command list, users can use the "help" command.

### Music
The bot is capable of playing the audio from youtube videos using [Lavaplayer](https://github.com/sedmelluq/lavaplayer).

### Automatic Role
The bot automatically gives a default role to every new member of the server.

### Games
The bot can be used to play some games in the chat. The games avaliable are the following:

#### Musicle
Based on the [Musicle site](https://musicle.app) the bot randomly selects a song from the selected music genre, plays it and gives five possible answers for the song name(or artist). The player then has 30 seconds to choose the one answer he thinks is the song playing.
It is also possible for multiple people to play the same song at the same time by using a musicle lobby.

#### TicTacToe
TicTacToe game played on chat by using buttons. Players can only play against each other and not against the CPU.

#### Chess
Chess game played by typing the piece positions in the chat. The board is given as a image, created using AWT's Graphic2D Class. Players can only play against each other and not against the CPU.
