# scrabble
A spring boot application that is setup to be a perfect scrabble game application.

# requirements
- MySQL 5.6.19+
- Java/JRE 1.8+
- Maven 3.5.3+

# setup
Before running the application MySQL needs to be setup. Install MySQL and create an user "root" with no password.
Give the "root" user the permission to create/remove tables.<br/>
Create the following databases:
  - scrabble_db
  - scrabble_db_test

Make sure MySQL is running in port 3306 before the application starts.

# run application
Because the application needs to setup the initial database, the command to run the first time is different than the subsequent runs.
Make sure to change directory to the scrabble project folder before running the commands.
 - Run application first time<br/>
    `mvn spring-boot:run -Dspring-boot.run.arguments=--spring.datasource.initialization-mode=always`
 - Run application after first time<br/>
    `mvn spring-boot:run`

Once the application is run for the first time, it creates four test users by default with user id 1, 2, 3 and 4.

# run tests
Because the integration tests in the application needs to setup the initial database, the command to run the tests first time is different than the subsequent runs.
Make sure to cd to the scrabble project folder before running the commands.
 - Run test first time<br/>
    `mvn test -D --spring.datasource.initialization-mode=always`
 - Run test after first time<br/>
    `mvn test`

# dictionary
The application currently is setup to work with a SOWPODS and TWL06 dictionary. Both dictionary words are loaded by default when the application starts.
If you want to load only one dictionary then provide `--app.dictionaries` command line argument with either one of following values:
- SOWPODS
- TWL06

For example to load only SOWPODS dictionary on first run:<br/>
`mvn spring-boot:run -Dspring-boot.run.arguments=--spring.datasource.initialization-mode=always,--app.dictionaries=SOWPODS`<br/>
For subsequent runs:<br/>
`mvn spring-boot:run -Dspring-boot.run.arguments=--app.dictionaries="SOWPODS"`

All words played and the words formed during the play must be in the dictionary. Otherwise, the play is considered invalid.

## response JSON objects

### person
id - id of the person (integer)<br/>
userName - user name of the person (string)<br/>
firstName - first name of the person (string)<br/>
lastName - last name of the person (string)<br/>
```
{
    "id": 1,
    "userName": "scrabbleUser",
    "firstName": "scrabbler",
    "lastName": "champion"
}
```

### tile
row - row index of the tile. starts from 0 (integer) <br/>
column - column index of the tile. starts from 0 (integer) <br/>
value - character value of the tile. Empty tiles are represented by '.' (string) <br/>
boost - boost value of tile (integer) <br/>
charBoost - boost value of character used in tile (integer) <br/>
```
{
    "row": 0,
    "column": 0,
    "value": ".",
    "boost": 1,
    "charBoost": 1
}
```

### board
size - size of board (integer) <br/>
tiles - JSON array of [Tile JSON objects](#tile)
```
{
    "size": 2,
    "tiles": [
        {
            "row": 0,
            "column": 0,
            "value": ".",
            "boost": 1,
            "charBoost": 1
        },
        {
            "row": 0,
            "column": 1,
            "value": ".",
            "boost": 1,
            "charBoost": 1
        },
        {
            "row": 1,
            "column": 0,
            "value": ".",
            "boost": 1,
            "charBoost": 1
        },
        {
            "row": 1,
            "column": 1,
            "value": ".",
            "boost": 1,
            "charBoost": 1
        }
    ]
}
```

### player
person - [person JSON object](#person) representing the player<br/>
order - player order of the person in the game (integer). For example: player who goes first is 1, player who goes second is 2 and so on..<br/>
```
{
    "person": {
        "id": 1,
        "userName": "scrabbleUser",
        "firstName": "scrabbler",
        "lastName": "champion"
    },
    "order": 1
}
```

### game
id - id of the game (integer)<br/>
board - board JSON object representing the game<br/>
players - JSON array of [player JSON objects](#player)<br/>
state - state of game with integer value of 0 (Initialized), 1 (In progress), 2 (finished)<br/>
nextTurnPlayer - [player JSON object](#player) who makes the move next after the current player<br/>
```
{
    "id": 6,
    "board": {
        "size": 2,
        "tiles": [
            {
                "row": 0,
                "column": 0,
                "value": ".",
                "boost": 1,
                "charBoost": 1
            },
            {
                "row": 0,
                "column": 1,
                "value": ".",
                "boost": 1,
                "charBoost": 1
            },
            {
                "row": 1,
                "column": 0,
                "value": ".",
                "boost": 1,
                "charBoost": 1
            },
            {
                "row": 1,
                "column": 1,
                "value": ".",
                "boost": 1,
                "charBoost": 1
            }
        ]
    },
    "players": [
        {
            "person": {
                "id": 1,
                "userName": "scrabbleUser",
                "firstName": "scrabbler",
                "lastName": "champion"
            },
            "order": 1
        }
    ],
    "state": 0,
    "nextTurnPlayer": {
        "person": {
            "id": 1,
            "userName": "scrabbleUser",
            "firstName": "scrabbler",
            "lastName": "champion"
        },
        "order": 1
    }
}
```

### move
word - the word that was played in the move (string)<br/>
row - the row index for the move (integer)<br/>
column - the column index for the move (integer)<br/>
player - the player JSON object who made the move<br/>
direction - the move direction id (integer). 0 for Left to Right and 1 for Top to Bottom.<br/>
points - the total points for the move (integer).<br/>
time - the long value for the time of the move. Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
```
{
    "word": "test",
    "row": 0,
    "column": 0,
    "player": {
        "person": {
            "id": 1,
            "userName": "scrabbleUser",
            "firstName": "scrabbler",
            "lastName": "champion"
        },
        "order": 1
    },
    "direction": 0,
    "points": 4,
    "time": 1541125957000
}
```

### games
games - JSON array of [game JSON object](#game)<br/>
count - total number of games for the request (integer)<br/>
limit - limit specified for the request (integer)<br/>
offset - offset specified for the request (integer)<br/>

### moves
moves - JSON array of [move JSON object](#move)<br/>
count - total number of move for the request (integer)<br/>
limit - limit specified for the request (integer)<br/>
offset - offset specified for the request (integer)<br/>

## request JSON objects

### player parameter
id - id of the player (integer)<br/>
order - player order of the person in the game (integer)<br/>
```
{
    "id": 1,
    "order": 1
}
```

### start game payload JSON object
boardSize - size of the board for the game (integer). Optional and defaults to 15 if not specified.<br/>
players - JSON array of [player request JSON object](#player-parameter). At least one payer request JSON object is required.<br/>
tilesPerPlayer - maximum number of tiles for each player for the game. Optional and defaults to square of board size if not specified.<br/>
```
{
    "boardSize": "20",
    "players": [{
    	"id": 1,
    	"order": 1
    }, {
        "id": 2,
        "order": 2
    }],
    "tilesPerPlayer": "100"
}
```

### update game payload JSON object
boardSize - size of the board for the game (integer).<br/>
players - JSON array of [player request JSON object](#player-parameter).<br/>
state - state of the game (integer). 0 represents initialized (new game), 1 represents in-progress game, 2 represents finished game.<br/>
```
{
    "boardSize": "20",
    "players": [{
    	"id": 1,
    	"order": 1
    }, {
        "id": 2,
        "order": 2
    }],
    "state": 0
}
```

### make move payload JSON object
word - the word that the player intends to play for the move (string). Required.<br/>
row - the row index of the board from which the word starts (integer). 0 based. Required.<br/>
column - the column index of the board from which the word starts (integer). 0 based. Required.<br/>
direction - the direction in which the word is played (integer). 0 for left to right. 1 for top to bottom.<br/>
player - the [player request JSON object](#player-parameter) representing the player making the move. Required<br/>
```
{
	"word": "test",
	"row": 0,
	"column": 0,
	"direction": 0,
	"player": {
		"id": 1,
		"order": 1
	}
}
```

### request query parameters
limit - number of results to get at a time (integer). Defaults to 50 for GET calls for collections. Maximum is 500. Defaults to maximum if more that maximum value is specified.<br/>
offset - the offset from which the limit applies for the result (integer). Defaults to 0.<br/>
state - the state of the game (integer). 0 represents initialized (new game), 1 represents in-progress game, 2 represents finished game.<br/>

### API's
| Purpose | Url | Method | Header |Request Body | Query Parameters | Response Body | Success response | Status Code |
|---------|-----|--------|--------|-------------|------------------|---------------|------------------|-------------|
| Start a new game | `/api/1.0/games` | POST | Content-Type: application/json | [Start game payload JSON object](#start-game-payload-json-object) | N/A | [Game response JSON object](#game) for the game that is created | 201 |
| Get list of games | `/api/1.0/games` | GET | N/A | N/A |<ul><li>limit</li><li>offset</li><li>state - Multiple states can be specified as state=0&state=1 </li></ul> | [Games response JSON object](#game) | 200 |
| Update a game | `/api/1.0/games/{game-id}` | PUT | Content-Type: application/json | [Update game payload JSON object](#update-game-payload-json-object) | N/A | N/A | 204 |
| Delete a game | `/api/1.0/games/{game-id}` | DELETE | N/A | N/A | N/A | 204 |
| Get game by id | `/api/1.0/games/{game-id}` | GET | N/A | N/A | N/A | [Game response JSON object](#game) for the game with the specified id | 200 |
| Make game move | `/api/1.0/games/{game-id}/move` | POST | Content-Type: application/json | [Make move payload JSON Object](#make-move-payload-JSON-object) | N/A | [Game response JSON object](#game) representing the latest state after the move | 200 |
| Get game history | `/api/1.0/games/{game-id}/history` | GET | N/A | N/A | - limit <br/> - offset <br/> | [Moves response JSON object](#moves) for the game ordered by move time in ascending order | 200 |
