Application programming interface
Application programming interfaces, or APIs, are what developers, fansite operators, and bots use to retrieve information about the game and the wiki.

Most of the official RuneScape APIs do not output the correct CORS response headers that would allow use of the API in front end code on an external website, and thus meaning you should make your calls from a dedicated backend.[1] However, CORS issues can be circumvented from within a Chrome extension, so creating a Chrome extension is a possibility.

Contents
The RuneScape Wiki
Bestiary API
Grand Exchange Database API
Configuration
Catalogue
category
items
detail
Images
Graph
Bulk data
Hiscores
Skills and Activities
ranking
userRanking
Hiscores Lite
Ironman Lite
Hardcore Ironman Lite
Seasonal
getRankings
getHiscoreDetails
Clans
clanRanking
userClanRanking
Clan Members Lite
Bosses
groups
Group Ironman
Old School Hiscores
Hiscores Lite
Ironman Lite
Hardcore Ironman Lite
Ultimate Ironman Lite
Deadman Mode Lite
Seasonal Lite
Tournament Lite
Fresh Start Worlds Lite
Solomon's General Store
Website Data
playerDetails
playerFriendsDetails
avatardetails (deprecated)
NPCs
Players
Runemetrics
Profile
Monthly xp
Quest
Other
player_count
rsusertotal
NXT
Developer Resources
References
The RuneScape Wiki
Main article: RuneScape:APIs
The RuneScape Wiki's API can be accessed here. The API Explorer special page can be used in addition to the documentation that MediaWiki has. Some features are limited to logged in users, administrators, and bureaucrats. The API Sandbox special page may be useful for users new to creating requests.

Bestiary API
Main article: RuneScape Bestiary § API
Grand Exchange Database API
The Grand Exchange Database, like the Bestiary API, is vast. It encompasses the Item Database which shows images of items not purchasable on the Grand Exchange itself and the catalogue information on tradeable items within the Grand Exchange. Queries in the API return JSON (JavaScript Object Notation).

A note for OSRS - if you wish to access Old School Runescape's Grand Exchange, change "m=itemdb_rs" to "m=itemdb_oldschool" in the URL. Along with this, there is only 1 category. A proper call to get page 1 of all items that start with the letter 'c' for the OSRS API would look like: https://secure.runescape.com/m=itemdb_oldschool/api/catalogue/items.json?category=1&alpha=c&page=1. Alpha is explained later in the wiki but it is the first letter of the item.
Configuration
info returns the runedate of when the Grand Exchange Database was last updated. The URL for the info query is https://secure.runescape.com/m=itemdb_rs/api/info.json.

Catalogue
category
category returns the number of items determined by the first letter. The URL for category queries is https://services.runescape.com/m=itemdb_rs/api/catalogue/category.json?category=X where X is the category identification number as listed below for all possible types. For example, if we want to find all tradeable summoning familiars, we'd go to https://services.runescape.com/m=itemdb_rs/api/catalogue/category.json?category=9, which would result in

{"types":[],"alpha":[{"letter":"#","items":0},{"letter":"a","items":6},{"letter":"b","items":8},{"letter":"c","items":1},{"letter":"d","items":3},{"letter":"e","items":2},{"letter":"f","items":3},{"letter":"g","items":5},{"letter":"h","items":2},{"letter":"i","items":5},{"letter":"j","items":0},{"letter":"k","items":1},{"letter":"l","items":2},{"letter":"m","items":5},{"letter":"n","items":1},{"letter":"o","items":1},{"letter":"p","items":4},{"letter":"q","items":0},{"letter":"r","items":3},{"letter":"s","items":27},{"letter":"t","items":2},{"letter":"u","items":1},{"letter":"v","items":5},{"letter":"w","items":2},{"letter":"x","items":0},{"letter":"y","items":0},{"letter":"z","items":0}]}
Variable	Description	Data type
letter	The first letter of an item	string
items	The number of items	int
items
items returns the first 12 items in the category given as shown below determined by the first letter. The URL for items queries are https://services.runescape.com/m=itemdb_rs/api/catalogue/items.json?category=X&alpha=Y&page=Z where X is the category identification number, Y is the starting letter (lower case) for the items and Z is the page number beginning at 1. Note that any items that start with a number must instead use %23 instead of #.

For example, if we want to find all tradeable summoning familiars that begin with "c", we'd go to https://services.runescape.com/m=itemdb_rs/api/catalogue/items.json?category=9&alpha=c&page=1, which would result in

{"total":90,"items":[{"icon":"https://services.runescape.com/m=itemdb_rs/1552302830305_obj_sprite.gif?id=12091","icon_large":"https://services.runescape.com/m=itemdb_rs/1552302830305_obj_big.gif?id=12091","id":12091,"type":"Familiars","typeIcon":"https://www.runescape.com/img/categories/Familiars","name":"Compost mound pouch","description":"I can summon a compost mound familiar with this.","current":{"trend":"neutral","price":987},"today":{"trend":"neutral","price":0},"members":"true"}]}
Variable	Description	Data type
total	The number of items	int
items	List of items	[string...] (array of strings)
icon	The item sprite image	string
icon_large	The item detail image	string
id	The ItemID of the item	int
type	The item category	string
typeIcon	The item image category	string
name	The item name	string
description	The item examine	string
current	The item trade history over the past day	[string...] (array of strings)
today	The item trade history for today	[string...] (array of strings)
trend	The positive, negative or neutral change in price	string
price	The item trade price	string
members	If the item is a member's only item	boolean
ID No.	Category Name
0	Miscellaneous
1	Ammo
2	Arrows
3	Bolts
4	Construction materials
5	Construction products
6	Cooking ingredients
7	Costumes
8	Crafting materials
9	Familiars
10	Farming produce
11	Fletching materials
12	Food and Drink
13	Herblore materials
14	Hunting equipment
15	Hunting Produce
16	Jewellery
17	Mage armour
18	Mage weapons
19	Melee armour - low level
20	Melee armour - mid level
21	Melee armour - high level
22	Melee weapons - low level
23	Melee weapons - mid level
24	Melee weapons - high level
25	Mining and Smithing
26	Potions
27	Prayer armour
28	Prayer materials
29	Range armour
30	Range weapons
31	Runecrafting
32	Runes, Spells and Teleports
33	Seeds
34	Summoning scrolls
35	Tools and containers
36	Woodcutting product
37	Pocket items
38	Stone spirits
39	Salvage
40	Firemaking products
41	Archaeology materials
42	Wood spirits
43	Necromancy armour
detail
detail returns current price and price trends information on tradeable items in the Grand Exchange, the category, item image and examine for the given item. The URL for detail queries is https://services.runescape.com/m=itemdb_rs/api/catalogue/detail.json?item=X where X is the ItemID.

For example, if we want the information on steadfast boots, we'd go to https://services.runescape.com/m=itemdb_rs/api/catalogue/detail.json?item=21787, which would result in

{"item":{"icon":"https://services.runescape.com/m=itemdb_rs/4856_obj_sprite.gif?id=21787","icon_large":"https://services.runescape.com/m=itemdb_rs/4856_obj_big.gif?id=21787","id":21787,"type":"Miscellaneous","typeIcon":"https://www.runescape.com/img/categories/Miscellaneous","name":"Steadfast boots","description":"A pair of powerful-looking boots.","current":{"trend":"neutral","price":"17.0m"},"today":{"trend":"negative","price":"-897.2k"},"members":"true","day30":{"trend":"positive","change":"+27.0%"},"day90":{"trend":"positive","change":"+30.0%"},"day180":{"trend":"positive","change":"+8.0%"}}}
Variable	Description	Data type
item	Lists the data for the item	string
icon	The item sprite image	string
icon_large	The item detail image	string
type	The item category	string
typeIcon	The item image category	string
name	The item name	string
description	The item examine	string
members	If the item is a member's only item	boolean
trend	The positive, negative or neutral change in price	string
price	The item trade price	string
change	The item fluctuation over the past 30 days	string
current	The item trade history over the past day	[string...] (array of strings)
today	The item trade history for today	[string...] (array of strings)
day30	The item trade history over 30 days	[string...] (array of strings)
day90	The item trade history over 90 days	[string...] (array of strings)
day180	The item trade history over 180 days	[string...] (array of strings)
Images
The Grand Exchange Database can return item images with obj_big and obj_sprite. These images can sometimes be skewed since it includes items that might not necessarily be seen in game such as placeholder graphics for most cosmetic overrides from Solomon's General Store, use the mtx pet icon for various pets and the achievement banner in other cases. Many items from Treasure Hunter and Solomon's General Store can be seen before being released in-game by sometimes over a month ahead.

The URL for obj_big queries is https://services.runescape.com/m=itemdb_rs/obj_big.gif?id=X where X is the ItemID.
The URL for obj_sprite queries is https://services.runescape.com/m=itemdb_rs/obj_sprite.gif?id=X where X is the ItemID.
For example, if we want to see the image for Mod Daze's homework, we'd go to https://services.runescape.com/m=itemdb_rs/obj_big.gif?id=34775 for a detailed image or https://services.runescape.com/m=itemdb_rs/obj_sprite.gif?id=34775 for the inventory icon. Unlike the images as seen in the game, the Grand Exchange Database images have a completely solid line surrounding items, which is different from ingame since there are breaks in images for borders.

Note that item images retrieved outside of the above place the most recent runedate before obj_big and obj_sprite. When the next system update occurs and info.json is updated, the link no longer operates for those that had a runedate in the link.

Graph
Graph shows the prices each day of a given item for the previous 180 days. The timecode is the number of milliseconds that has passed since 1 January 1970 and the price is the market value of the item for the given day. When no price information is available, then a value of zero is returned.

The URL for a graph is https://services.runescape.com/m=itemdb_rs/api/graph/X.json where X is the ItemID. For example, if we want to know the last 180 days for steadfast boots, we'd go to https://services.runescape.com/m=itemdb_rs/api/graph/21787.json, where the previous three days, truncated for this article, would start out as

{"daily":{"1419897600000":15633853,"1419984000000":15475988,"1420070400000":15379017},"average":{"1419897600000":14708793,"1419984000000":14764787,"1420070400000":148288055}}
Variable	Description	Data type
daily	The item trade history over the past day (Grand Exchange value at the given timestamp)	[string...] (array of strings)
average	The item trade history for today (30 day moving average for the given timestamp)	[string...] (array of strings)
timecode	The first number showing the milliseconds since 1 January 1970	int
price	The second number showing the exact price and the average price	int
Bulk data
There's almost no scenario where you should be looping over every item if you need bulk GE data. If you need bulk GE data, see: RuneScape:Grand Exchange Market Watch/Usage and APIs#Bulk data API.

Hiscores
The hiscores can show a players current ranking in their clan, any skill or activity and active and past seasonal events.

Skills and Activities
ranking
ranking returns up to the top 50 players in a given skill or activity. The URL for ranking is https://secure.runescape.com/m=hiscore/ranking.json?table=X&category=Y&size=Z where X is the current skill, overall level or activity, Y are skills or activities and Z is the amount requested up to the top 50 players.

For example, if we want to know the top 2 players in the Woodcutting skill we would go to https://services.runescape.com/m=hiscore/ranking.json?table=9&category=0&size=2, which would result in

[{"name":"Elfinlocks","score":"200,000,000","rank":"1"},{"name":"Cow1337killr","score":"200,000,000","rank":"2"}]
Variable	Description	Data type
name	The player's name	string
score	The player's experience or score	int
rank	The player's rank	int
userRanking
userRanking returns the current logged in player's rank in the overall hiscores table. The URL for userRanking is https://secure.runescape.com/c=0/m=hiscore/userRanking.json where the 0 (in c=0) needs to be the current session id of the player to view their overall rank.

Variable	Description	Data type
userRank	The player's overall rank	string
displayName	The player's name	string
Hiscores Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore/index_lite.ws?player=X where X is the player's name.

Sample response (with line breaks)
Each skill/category is on a new line (split by a new line delimiter: \n), and each line has three comma-separated values:

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
The skills in order are:

Overall, Attack, Defence, Strength, Constitution, Ranged, Prayer, Magic, Cooking, Woodcutting, Fletching, Fishing, Firemaking, Crafting, Smithing, Mining, Herblore, Agility, Thieving, Slayer, Farming, Runecrafting, Hunter, Construction, Summoning, Dungeoneering, Divination, Invention, Archaeology, Necromancy.

Followed by activities:

Bounty Hunter, B.H. Rogues, Dominion Tower, The Crucible, Castle Wars games, B.A. Attackers, B.A. Defenders, B.A. Collectors, B.A. Healers, Duel Tournament, Mobilising Armies, Conquest, Fist of Guthix, GG: Athletics, GG: Resource Race, WE2: Armadyl Lifetime Contribution, WE2: Bandos Lifetime Contribution, WE2: Armadyl PvP kills, WE2: Bandos PvP kills, Heist Guard Level, Heist Robber Level, CFP: 5 game average, AF15: Cow Tipping, AF15: Rats killed after the miniquest, RuneScore, Clue Scrolls Easy, Clue Scrolls Medium, Clue Scrolls Hard, Clue Scrolls Elite, Clue Scrolls Master

Ironman Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_ironman/index_lite.ws?player=X where X is the player's name.

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
See above for an example of the response

Hardcore Ironman Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_hardcore_ironman/index_lite.ws?player=X where X is the player's name.

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
See above for an example of the response

Seasonal
getRankings
getRankings returns the player's current or previous scores in one or more Seasonal HiScores. The URL for getRankings is http://services.runescape.com/m=temp-hiscores/getRankings.json?player=X where X is the player's name. It can also be used to view previous scores for past seasonals with the URL http://services.runescape.com/m=temp-hiscores/getRankings.json?player=X&status=archived.

Variable	Description	Data type
startDate	The seasonal start date	string
endDate	The seasonal end date	string
rank	The player's rank	int
title	The title of the seasonal	string
score_formatted	The player's score	int
score_raw	The player's raw score	int
hiscoreId	The id of the seasonal	int
getHiscoreDetails
getHiscoreDetails returns the current or previous Seasonal events. The URL for getHiscoreDetails is http://services.runescape.com/m=temp-hiscores/getHiscoreDetails.json and http://services.runescape.com/m=temp-hiscores/getHiscoreDetails.json?status=archived for previous events.

Variable	Description	Data type
startDate	The seasonal start date	string
endDate	The seasonal end date	string
daysRunning	The number of days the seasonal is on for	int
monthsRunning	The number of months the seasonal is on for	int
recurrence	The number of times the seasonal has ran for	int
title	The title of the seasonal	string
name	The literal name of the seasonal	string
description	The description of the seasonal	string
status	The current status of the seasonal	string
type	The length of the seasonal	string
id	The id of the seasonal	int
Clans
clanRanking
clanRanking returns the top three clans. The URL for clanRanking is http://services.runescape.com/m=clan-hiscores/clanRanking.json.

Variable	Description	Data type
rank	The rank of the clan	int
clan_name	The name of the clan	string
clan_mates	The number of players in the clan	int
xp_total	The total experience of the clan	int
userClanRanking
userClanRanking returns the current logged in player's clan and the rank of the clan. The URL for userClanRanking is http://services.runescape.com/c=0/m=clan-hiscores/userClanRanking.json where the 0 (in c=0) needs to be the current session id of the player to view their clan's rank.

Variable	Description	Data type
displayName	The player's name	string
clanName	The clan's name	string
clanRank	The clan's rank	int
Clan Members Lite
members_lite.ws returns a CSV file with a list of clan members, their rank in the clan, their total experience and total kills. The URL for members_lite.ws is http://services.runescape.com/m=clan-hiscores/members_lite.ws?clanName=X where X is the name of the clan. The list is sorted by clan ranking starting with Owner and ending with Recruit.

Bosses
groups
The URL for groups is https://secure.runescape.com/m=group_hiscores/v1//groups?groupSize=A&size=B&bossId=C&page=D where A is the size of the group fighting the boss, B is the amount of entries returned per-page, C is the boss, and D is the page number.

For example, to get the top duo Zamorak, Lord of Chaos kill, https://secure.runescape.com/m=group_hiscores/v1//groups?groupSize=2&size=1&bossId=1&page=0 can be used, which would result in:

{"content":[{"id":44,"bossId":1,"size":2,"rank":1,"enrage":30,"killTimeSeconds":285.0,"timeOfKill":1656950518,"members":[{"id":833211758648080823,"name":"Saif0"},{"id":98543719497987008,"name":"DePose"}]}],"totalElements":252,"totalPages":252,"first":true,"last":false,"numberOfElements":1,"number":0,"size":1,"empty":false}
Variable	Description	Data type
content	The list of boss kill entries	[object...] (array of objects)
totalElements	The total entries on this board (boss id + group size)	int
totalPages	The total pages given your current size param	int
first	If this page includes the first entry on the board	bool
last	If this page includes the last entry on the board	bool
numberOfElements	The length of the content array	int
number	The page number	int
size	The max amount of entries per page	int
empty	Whether the content array is empty	bool
Objects in the content array:

Variable	Description	Data type
id	The id of this entry	int
bossId	The id of the boss	int
size	The size of the group	int
rank	The entry's rank on the board	int
enrage	The enrage this kill was done at	int
killTimeSeconds	The duration to complete the kill in seconds	float
timeOfKill	The unix timestamp in seconds when the kill finished (multiply by 1000 for unix timestamp)	int
members	The members of the group	[object...] (array of objects)
Objects in the members array:

Variable	Description	Data type
id	The id of this player	int
name	The name of this player	string
Group Ironman
The URL for GIM is https://secure.runescape.com/m=runescape_gim_hiscores//v1/groupScores?groupSize=A&size=B&page=C&isCompetitive=D where A is the size of the group, B is the amount of entries returned per-page, C is the page number, and D is whether the group is in competitive mode.

For example, to get the top duo competitive GIM hiscores, https://secure.runescape.com/m=runescape_gim_hiscores//v1/groupScores?groupSize=2&size=3&page=0&isCompetitive=true can be used, which would result in:

{"totalElements":173,"totalPages":173,"size":1,"content":[{"id":403054,"name":"Plink","groupTotalXp":2300,"groupTotalLevel":92,"size":2,"toHighlight":false,"isCompetitive":true,"founder":true}],"first":true,"last":false,"numberOfElements":1,"pageNumber":0,"empty":false}
Variable	Description	Data type
content	The list of group entries	[object...] (array of objects)
totalElements	The total entries on this board	int
totalPages	The total pages given your current size param	int
first	If this page includes the first entry on the board	bool
last	If this page includes the last entry on the board	bool
numberOfElements	The length of the content array	int
pageNumber	The page number	int
size	The max amount of entries per page	int
empty	Whether the content array is empty	bool
Objects in the content array:

Variable	Description	Data type
id	The id of this group entry	int
name	The name of this group	int
groupTotalXp	The total xp from all group members added together	int
groupTotalLevel	The total level from all groups members added together	int
size	The amount of players in the group	int
toHighlight	Used by other apis for indexing search results	bool
isCompetitive	Whether the group is in competitive mode	bool
founder	The founder status of the group	bool
Old School Hiscores
Hiscores Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=X where X is the player's name.

An alternate URL can be used to return the results in a JSON instead of a CSV, which also includes the corresponding activity name. The URL for this would be is https://secure.runescape.com/m=hiscore_oldschool/index_lite.json?player=X where X is the player's name.

Sample response (with line breaks)
Each skill/category is on a new line (split by a new line delimiter: \n), and each line has three comma-separated values:

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill or score	int
experience	The player's experience (not defined for activities)	int
The skills in order are:

Overall
Attack
Defence
Strength
Hitpoints
Ranged
Prayer
Magic
Cooking
Woodcutting
Fletching
Fishing
Firemaking
Crafting
Smithing
Mining
Herblore
Agility
Thieving
Slayer
Farming
Runecrafting
Hunter
Construction
Followed by activities:

League Points
Deadman Points
Bounty Hunter - Hunter
Bounty Hunter - Rogue
Bounty Hunter (Legacy) - Hunter
Bounty Hunter (Legacy) - Rogue
Clue Scrolls (all)
Clue Scrolls (beginner)
Clue Scrolls (easy)
Clue Scrolls (medium)
Clue Scrolls (hard)
Clue Scrolls (elite)
Clue Scrolls (master)
LMS - Rank
PvP Arena - Rank
Soul Wars Zeal
Rifts closed
Colosseum Glory
Collections Logged
Abyssal Sire
Alchemical Hydra
Amoxliatl
Araxxor
Artio
Barrows Chests
Bryophyta
Callisto
Cal'varion
Cerberus
Chambers of Xeric
Chambers of Xeric: Challenge Mode
Chaos Elemental
Chaos Fanatic
Commander Zilyana
Corporeal Beast
Crazy Archaeologist
Dagannoth Prime
Dagannoth Rex
Dagannoth Supreme
Deranged Archaeologist
Duke Sucellus
General Graardor
Giant Mole
Grotesque Guardians
Hespori
Kalphite Queen
King Black Dragon
Kraken
Kree'Arra
K'ril Tsutsaroth
Lunar Chests
Mimic
Nex
Nightmare
Phosani's Nightmare
Obor
Phantom Muspah
Sarachnis
Scorpia
Scurrius
Skotizo
Sol Heredit
Spindel
Tempoross
The Gauntlet
The Corrupted Gauntlet
The Hueycoatl
The Leviathan
The Whisperer
Theatre of Blood
Theatre of Blood: Hard Mode
Thermonuclear Smoke Devil
Tombs of Amascut
Tombs of Amascut: Expert Mode
TzKal-Zuk
TzTok-Jad
Vardorvis
Venenatis
Vet'ion
Vorkath
Wintertodt
Zalcano
Zulrah
Ironman Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_oldschool_ironman/index_lite.ws?player=X where X is the player's name.

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
See above for an example of the response

Hardcore Ironman Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_oldschool_hardcore_ironman/index_lite.ws?player=X where X is the player's name.

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
See above for an example of the response

Ultimate Ironman Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_oldschool_ultimate/index_lite.ws?player=X where X is the player's name.

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
See above for an example of the response

Deadman Mode Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_oldschool_deadman/index_lite.ws?player=X where X is the player's name.

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
See above for an example of the response

Seasonal Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_oldschool_seasonal/index_lite.ws?player=X where X is the player's name. This url includes Deadman Seasonals as well as Leagues.

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
See above for an example of the response

Tournament Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_oldschool_tournament/index_lite.ws?player=X where X is the player's name.

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
See above for an example of the response

Fresh Start Worlds Lite
index_lite returns a player's rank, level and experience or score in an activity. The URL for index_lite is https://secure.runescape.com/m=hiscore_oldschool_fresh_start/index_lite.ws?player=X where X is the player's name.

Variable	Description	Data type
rank	The player's rank in a skill or activity	int
level	The player's level in a skill	int
experience	The player's experience or score	int
See above for an example of the response

Solomon's General Store
Solomon's General Store's configuration API lists every past, current and upcoming items for the store. The URL for the configuration query is https://secure.runescape.com/m=mtxn_rs_shop/api/config?context%5B0%5D=0 where context[0]=0 allows players to view the media in the store. It is unsorted without help from secondary programs such as Google Chrome's developer tools to organise the list.

The version number in the API represents the last time the store was updated in milliseconds since 1 January 1970. There are several other API's that the store uses, however they require players to be logged into the RuneScape site.

Variable	Description	Data type
_type	The type of action that the store uses to view the object	string
version	The latest update in milliseconds since 1 January 1970	int
url	The list of links that the store uses	[string...] (array of strings)
phrasebook	Similar to MediaWiki's i18n pages	[string...] (array of strings)
media	The list of objects that appear in the store	[string...] (array of strings)
number	The id number that the store uses to show the object	int
imageUrl	The image link to display the icon or a gallery image	string
posterUrl	The image link to display a still image if MP4 is unsupported	string
videoUrl	The video link to display the MP4 of an item in the store	string
Website Data
Website data can provide information on a player's friends, their titles and what clan they are in.

playerDetails
playerDetails returns multiple players' titles, their clan, their name and if the clan they are in is recruiting. The URL for playerDetails is https://secure.runescape.com/m=website-data/playerDetails.ws?names=%5B%22X%22%5D&callback=jQuery000000000000000_0000000000&_=0 where X is the first players name and addition names can be provided by using a comma after X and surrounding them with quotes inside the brackets.

If the request is performed as a JSONP object on the RuneScape site, it can give the world the player is on if the online status is true.

$.ajax({
  url: "https://secure.runescape.com/m=website-data/playerDetails.ws?names=[%22Name%22]",
  dataType: "jsonp"
});
Note: The world and online status is only returned if the player is online on RuneScape and not Old School RuneScape. The player must also have their in-game online status set to "Everyone", and the player making the API call must be logged in on runescape.com (so that cookies are sent with the request).

Variable	Description	Data type
isSuffix	Determines if the player title is before the name	boolean
title	The player's title	string
clan	The clan's name	string
name	The player's name	string
recruiting	If the clan is recruiting players	boolean
world	The world or lobby a player is in	string
online	Determines if the player is online	boolean
playerFriendsDetails
playerFriendsDetails returns the logged-in player's own friends list. The URL for playerFriendsDetails is http://services.runescape.com/c=0/m=website-data/playerFriendsDetails.json?resultsPerPage=24&currentPage=1&callback=jQuery000000000000000_0000000000&_=0 where you must be logged in to view, the max results per request is 24 and the current page shows up to 24 players. It cannot be viewed as a web page. Depending on in-game settings, even when a player is online they might not appear to be online if Private is set to off.

Variable	Description	Data type
allFriends	Shows the total number of players a friend has	int
friends	The player's title	[string...] (array of strings)
number	Friends list sorted by most recently online	int
status	Shows if a player is online or offline	string
name	The player's name	string
world	The world or lobby a player is in	string
offline	The amount of friends offline	int
online	The amount of friends online	int
pageFriends	The amount of friends on the page	int
resultsPerPage	The amount of results per page	int
totalPages	The total pages the friends list span	int
avatardetails (deprecated)
avatardetails returns a player's item information worn when taking a picture at the Photo booth for their Adventurer's Log. The URL for avatardetails is http://services.runescape.com/m=adventurers-log/avatardetails.json?details=X where X is the content of the appearance.dat for the player being viewed. The URL for appearance.dat is http://services.runescape.com/m=avatar-rs/X/appearance.dat where X is the player's name.

Can also bring up the avatar picture by using http://secure.runescape.com/m=avatar-rs/X/chat.png where X is the player's name. Use %20 for players names with spaces.

NPCs
Using appearance.dat, players can view the appearances of Jagex Moderators and other players easily. It allows anyone to see any monster, override, equipable items and the item information as an indirect item API. Strings are in the form of bits from 0 to 63 where the order is A-Z, a-z, 0-9, then - and *.

When an NPC is viewed, there is no information in avatardetails.json and the animation for the NPC. In a javascript console, players can view any NPC that is in the game with:

Module.avatar.module.ccall("SetAppearance", "void", ["string"], ["AP--AAA-----------------"]);
Where AAA is the NPC ID starting at 0 (Hans) and the next NPC starting at AAE as each NPC takes up four bits. The seventeen dashes following the ID are necessary to prevent from getting bad request messages in their console.

In a javascript console, when clicking on 'Idle' does not work, players can fix the animation of any NPC by using:

Module.avatar.SetAnimID(0)
Where 0 is the default structure of every NPC. It can sometimes be substituted for the animation IDs for that NPC if they are shown in their bestiary entry. Players might have to re-enter the SetAppearance string again to correct any issues.

Players
In a javascript console, players can view any other player with:

avatarViewer.avatarChange("X")
Where X is the other players' character name. This allows a player to view Jagex Moderators and other players that have privacy settings activated. Regardless of privacy settings, along with the full character image and chathead for the forums, the appearance.dat file is always viewable.

Each string can be changed to show equipable items that were not worn during the usage of the Photo Booth. The only way to use a custom string is in a javascript console by using:

Module.avatar.module.ccall("SetAppearance", "void", ["string"], ["AAAAAAABcgABHAEmAWEBIgEqA*SzIwAAAAAAABAQyAAAAAAAAAAAAAAAAAAAAAAKiwAAAA"]);
Where the string of letters and numbers can be modified to show different hair styles, default clothing, and worn items.

The only item worn in this string is an aura, Daemonheim aura 4, noted by the string zIw. Not all strings are four letters and numbers as many are three letters and numbers. It changes the more items that are visible and whether or not they are recolourable can add complexity to the string.

Variable	Description	Data type
leftAnims	List of animations	[string...] (array of strings)
rightAnims	List of animations	[string...] (array of strings)
attackAnim	Attack animation	int
attackAnimLegacy	Legacy attack animation	int
attackAnimOverride	Attack animation override	int
attackAnimOverrideLegacy	Legacy attack animation override	int
defendAnim	Defence animation	int
defendAnimLegacy	Legacy defence animation	int
defendAnimOverride	Defence animation override	int
worn	Lists the worn items	[string...] (array of strings)
desc	Item examine	string
members	If the item is a member's only item	boolean
name	Item name	string
tradeable	If the item is tradeable	boolean
weight	The weight of the item	int
magicAttack	Magic accuracy	int
meleeAttack	Melee accuracy	int
rangedAttack	Ranged accuracy	int
meleeStrength	Melee damage	int
magicDefence	Defence against magic	int
meleeDefence	Defence against melee	int
rangedDefence	Defence against ranged	int
playerArmour	The armour value	int
combatAppearance	-	string
combatBas	Lists basID for EoC and Legacy	[string...] (array of strings)
basID	-	int
basIDLegacy	-	int
ID No.	Worn Slot
0	Head
1	Cape
2	Neck
3	Main-hand (unsheathed)
4	Body
5	Off-hand (unsheathed)
6	Two-handed (unsheathed)
7	Legwear
8	N/A
9	Hands
10	Feet
11	N/A
12	Ring
13	Ammunition
14	Aura
15	Main-hand (sheathed)
16	Off-hand (sheathed)
17	Pocket
18	Wings
Runemetrics
Since Runemetrics is an Angular application, it uses public endpoints to grab its data from the server. This opens up the possibility to directly pull data from said endpoints in your own application.

Since these endpoints are not official API URL's, they don't seem to have the strict request throttling that the regular API has.

Profile
/profile returns a player's Runemetrics profile data. This includes name, total experience, skill levels, combat experience and activity. The URL is https://apps.runescape.com/runemetrics/profile/profile?user=X&activities=20 where X is the player's name.

Variable	Description	Data type
name	The player's name	string
rank	The player's overall rank	string
totalskill	The player's total skill level	int
totalxp	The player's total experience	int
combatlevel	The player's combat level	int
magic	The player's magic experience	int
melee	The player's melee experience	int
ranged	The player's ranged experience	int
questsstarted	The amount of quests the player has started	int
questscomplete	The amount of quests the player has completed	int
questsnotstarted	The amount of quests the player has not started	int
activities	The player's last 20 activity logs	array
skillvalues	The player's level, experience and rank in each skill (see below for skill IDs)	array
loggedIn	Indicates if the player is online (using a string representation of a boolean: "true"/"false")	string
ID no.	Skill name
0	Attack
1	Defence
2	Strength
3	Constitution
4	Ranged
5	Prayer
6	Magic
7	Cooking
8	Woodcutting
9	Fletching
10	Fishing
11	Firemaking
12	Crafting
13	Smithing
14	Mining
15	Herblore
16	Agility
17	Thieving
18	Slayer
19	Farming
20	Runecrafting
21	Hunter
22	Construction
23	Summoning
24	Dungeoneering
25	Divination
26	Invention
27	Archaeology
28	Necromancy
Monthly xp
/xp-monthly returns the monthly exp gains for the specified player and skill. This data includes the amount of experience obtained in the last 12 months, the average exp for the selected skill, the total amount of exp gained within the last 12 months and the total exp in the selected skill. The URL is https://apps.runescape.com/runemetrics/xp-monthly?searchName=X&skillid=Y where X is the player's name and Y is the id of the skill.

The response contains the object monthlyXpGain which will be explained below and a string ("true"/"false") for loggedIn which is only present when not signed in to Runemetrics (this does not indicate whether a player is online or not).

Variable	Description	Data type
averageXpGain	The average xp obtained over a period of 12 months	int
monthData	The xp that was achieved each month	array
skillId	The id of the skill	int
totalGain	The total amount of exp earned within the last 12 months	int
totalXp	The total amount of exp earned	int
Quest
/quests returns a player's quest data. This includes a list of all quests, their completion status, difficulty and quest point reward. The URL is https://apps.runescape.com/runemetrics/quests?user=X where X is the player's name

The response is an array of Objects. Each object has the following structure

Variable	Description	Data type
title	The quest's title	string
status	The completion status of this quest for the player	string
difficulty	The quest's difficulty	integer
members	Determines if a quest is members content	boolean
questPoints	Amount of quest points it rewards	integer
userEligible	Determines if the player is eligible to start this quest	boolean
Other
player_count
player_count returns the number of players currently online in RuneScape and Old School RuneScape. The URL for player_count is http://www.runescape.com/player_count.js?varname=iPlayerCount&callback=jQuery000000000000000_0000000000&_=0 where the varname is always iPlayerCount.

rsusertotal
rsusertotal returns the current amount of accounts created that can access any form of RuneScape. This includes accounts made on FunOrb or a particular version of RuneScape. The URL for rsusertotal is https://secure.runescape.com/m=account-creation-reports/rsusertotal.ws.

NXT
changelog returns the latest changes to the NXT client. The URL for changelog is http://content.runescape.com/downloads/changelog.json.

There is also the JSON for the OSX and Windows installers detailing the current size of the installer and the cycling redundancy check code.

http://content.runescape.com/downloads-info/windows/RuneScape-Setup.exe.json
http://content.runescape.com/downloads-info/osx/RuneScape.dmg.json
Developer Resources
Main article: /Resources
References
^ Shalvah. Hacking It Out: When CORS won’t let you be great. 21 August 2017. (Archived from the original on 20 June 2019.)

# OSRS Grand Exchange API Documentation

## Overview

This document details the APIs used by GE-Prospector for retrieving Grand Exchange data and item information.

## Primary APIs

### 1. OSRS Wiki Prices API
Base URL: `https://prices.runescape.wiki/api/v1/osrs`

#### Endpoints

##### Latest Prices
```
GET /latest
```
Returns latest prices for all items.

Response format:
```json
{
    "data": {
        "item_id": {
            "high": int,
            "highTime": int,
            "low": int,
            "lowTime": int
        }
    }
}
```

##### 5 Minute Average
```
GET /5m
```
Returns 5-minute average prices.

##### 1 Hour Average
```
GET /1h
```
Returns 1-hour average prices.

##### Time Series
```
GET /timeseries
```
Parameters:
- timestep: [5m, 1h, 6h]
- id: item ID

### 2. RuneLite Item API

#### Item Information
```
GET /runelite-{version}/item/{itemId}
```
Returns detailed item information.

#### Item Icons
```
GET /cache/item/icon/{itemId}
```
Returns item sprite image.

### 3. Official OSRS GE API
Base URL: `https://secure.runescape.com/m=itemdb_oldschool/api`

#### Item Details
```
GET /catalogue/detail.json?item={itemId}
```
Returns official GE data.

## Implementation Guidelines

### Rate Limiting
- Wiki API: 300 requests per minute
- RuneLite API: No strict limit, use responsibly
- OSRS API: 100 requests per minute

### Caching Requirements
1. Price Data
   - Latest prices: 1 minute
   - Averages: 5 minutes
   - Historical data: 1 hour

2. Item Data
   - Item information: 24 hours
   - Icons: 7 days
   - Market trends: 15 minutes

### Error Handling

#### HTTP Status Codes
- 200: Success
- 429: Rate limit exceeded
- 503: Service unavailable
- 504: Gateway timeout

#### Recovery Strategies
1. Rate Limiting
   - Implement exponential backoff
   - Queue requests
   - Use bulk endpoints where possible

2. Service Unavailable
   - Fallback to cached data
   - Retry with backoff
   - Alert user if persistent

### Data Processing

#### Price Calculations
1. Profit Margins
```java
margin = sellPrice - buyPrice - (sellPrice * 0.01)
```

2. Volume Metrics
```java
volumeScore = (hourlyVolume * priceStability) / competitionFactor
```

3. Time Estimates
```java
estimatedMinutes = baseTime * volumeFactor * timeOfDayFactor
```

### Best Practices

1. API Calls
   - Use bulk endpoints when possible
   - Implement proper caching
   - Handle rate limits gracefully
   - Log all API errors

2. Data Management
   - Validate responses
   - Handle missing data
   - Implement fallbacks
   - Monitor API health

3. Updates
   - Poll latest prices every minute
   - Update averages every 5 minutes
   - Refresh item data daily
   - Clean cache weekly

## Testing

### API Tests
1. Connection Tests
   - Endpoint availability
   - Response times
   - Rate limit handling
   - Error responses

2. Data Tests
   - Response validation
   - Data consistency
   - Cache effectiveness
   - Update frequency

### Mock Data
- Sample responses in `src/test/resources/api-responses/`
- Test scenarios in `src/test/resources/test-cases/`
- Rate limit simulations
- Error condition mocks

## Security

### API Authentication
- Include user agent
- Respect rate limits
- Handle keys securely
- Monitor usage

### Data Protection
- Validate responses
- Sanitize data
- Encrypt sensitive info
- Secure storage

## Monitoring

### Health Checks
- API availability
- Response times
- Error rates
- Cache hits/misses

### Alerts
- Rate limit warnings
- Service disruptions
- Data anomalies
- Performance issues

## Support

### Resources
- Wiki API Discord
- RuneLite Discord
- OSRS Developer Forum
- GitHub Issues
Category: Mechanics
Navigation menu
19:36
Not logged in
Talk
Contributions
Sign up
Log in
ArticleDiscussion
EditEdit sourceHistory

More
Search the RuneScape Wiki
Search
Discord
Navigation
About us
User help
Random page
Recent changes 
Daemonheim recipe
20m ago - Grandfear

Fairy ring
21m ago - Gbear605

Dino food
22m ago - Grandfear

Woodcutting
27m ago - Kasper.franz

Show more...
Guides
List of quests
Skill training
Money making
Treasure Trails
New player guide
Databases
PvM portal
Calculators
Bestiary
Achievements
Editing
How to edit
Policies
Projects
New files
The Wikian
Community
Discussions
Clan Chat
More RuneScape
OSRS Wiki
RSC Wiki
Wiki Trivia Games
RuneScape.com
Tools
What links here
Page information
Browse properties
Make new page
Report Ad
This page was last modified on 29 January 2025, at 23:18.
Content on this site is licensed under CC BY-NC-SA 3.0; additional terms apply.
RuneScape and RuneScape Old School are the trademarks of Jagex Limited and are used with the permission of Jagex.
Privacy policyAbout the RuneScape WikiDisclaimersTerms of UseContact Weird GloopMobile view
CC BY-NC-SA 3.0A Weird Gloop wiki

RuneScape:Grand Exchange Market Watch/Usage and APIs
< RuneScape:Grand Exchange Market Watch
This guide applies to both the RSW (including pt-br RSW) and OSRSW.

Grand Exchange prices are a very common thing to want to use, both within articles and externally. This is a guide to using them in many contexts.

This guide covers the four main areas of using GE prices: on wiki articles via templates, within a lua module, within wiki javascript, and externally. There is also a section on understanding the data structure of the price data on the wiki.


Contents
Using GEMW in wiki templates
Basic
Additional
Supporting
Understanding GEMW
Exchange pages
Information module
Fresh Start Worlds
Bulk data pages
Bulk data API
Exchange API
Exchange API for indices
A note on volumes
Using GEMW in wiki modules
Loading prices
Wiki javascript
Making the wiki do the loading
Javascript loading
External usage
Using the API
Google Sheets
Querying the wiki
Bulk pages
Other queries
Using GEMW in wiki templates
Using these templates doesn't necessarily require any understanding of the data structure, which is why that is the next section.

Basic
The most common usage on the wiki is in an article directly, which is easily done via templates. We have two templates for simply using a GE price:

Template:GEP
{{GEP|item}}
Outputs the price of item.

{{GEP|item|number}}
Outputs the price of item times number - handy syntactic sugar to skip a normal multiplication. Number can be fractional or negative!

Template:GEPrice
{{GEPrice|item}}
Outputs the price of item with thousands separators if necessary (which will be commas as this is an English wiki).

Additional
We have a few extra templates to simplify some operations with GE prices, us them if you wish.

Template:GETotal
{{GETotal|item1|item2|item3|...}}
Outputs the sum of the prices of one of each of item1, item2, .... No limit on number of items.

Template:GETotalqty
{{GETotalqty|name1=item1|qty1=#1|name2=item2|qty2=#2|...|name20=item20|qty20=#20}}
Outputs the sum of #X times itemX for X from 1 to 20. i.e., its GETotal but you can specify the quantity of each item individually.

Supporting
These are not strictly GE price fetchers, but are useful for working with prices.

#expr
{{#expr: expression}}
The basic parser function that will calculate a mathematical expression. Will throw an error if something is not right with the expression.

eg {{#expr:{{GEP|Shortbow}} - {{GEP|Logs}} - {{GEP|Flax}}}}

formatnum
{{formatnum: number}}
Formats the number with thousands separators.

Template:fe
{{fe|expression}}
Syntactic sugar to combine #expr with formatnum to calculate and expression then format the result with thousands separators.

Template:Coins
{{Coins|expression}}
Calculates expression (with #expr) then formats the result with an image of coins and a colour representing profit/loss. Generally should be used in tables and not prose.

Template:NoCoins
{{NoCoins|expression}}
Like Template:Coins but without the image. As the image can distrupt the layout of prose, this template is preferred to Coins.

{{NoCoins|expression|c}}
Adds the word 'coins' after the result, which is also coloured.n

Understanding GEMW
Understanding the data structure is important for more technical uses of the data. There are three main pages types involved with GE prices. Every item has one of each.

It is worth noting that GEMW names are usually strictly at the item's actual name - unless it needed to disambiguated - and each GEMW name maps to only one item ID (and usually vice versa, but sometimes not as strictly as it should be).

Some examples:

Exchange:Fire rune - osrsw:Exchange:Fire rune
While prayer potion has information on all 4 doses (and you would use prayer potion#(2) to refer to a specific dosage), the GEMW names are Exchange:Prayer potion (2) etc.
At some point in RS, the space was introduced - OSRS does not have a space before the dosage, so the GEMW name is osrsw:Exchange:Prayer potion(2)
In the Mining and Smithing rework, new IDs of a lot of smithable items were introduced (and the old IDs removed from the GE), so the old data has been disambiguated to Exchange:Steel platelegs (historical), and the new replaces it at Exchange:Steel platelegs
While OSRS hasn't had such an occurence, there are still some historical GE items, e.g. osrsw:Exchange:Armillary sphere (flatpack)
For a list of all historical GE items, see Category:Historical Grand Exchange - osrsw:Category:Historical Grand Exchange
Exchange pages
Exchange:Bones - osrsw:Exchange:Bones
Mostly a front end view of the data now. Historically the home of the data before the move to lua.

Exchange pages also have Semantic MediaWiki (SMW) properties set for much of the information (though intentionally nothing that actually changes often, like price). For example, Special:Browse/:Exchange:Bones (osrsw:Special:Browse/:Exchange:Bones). It is generally easier to reach browse by going to the normal Exchange page, then finding the "Browse properties" link toward the bottom of the left sidebar, under toolbox. To query this data, you can use Special:Ask; see Help:Semantic MediaWiki for information on using SMW.

Information module
Module:Exchange/Bones - osrsw:Module:Exchange/Bones
Contains all the 'static' information about the item, like value, buy limit, ID, etc. This is used by the GE bot to know what item name matches what item ID.

This is a lua module which returns a table, a data structure fairly similar to JSON (and easily parsed into it).

Prices are no longer part of this module, except for items marked with historical = true, which contain the last recorded price.

Fresh Start Worlds
A quick note on Fresh Start Worlds (including OSRS FSW):

As the majority of information is the same, we only need to provide prices, last prices, and volumes for it
The dump is also provided
Historical prices can be obtained from https://api.weirdgloop.org using the the game key rs-fsw-2022 and osrs-fsw-2022
For RS3 FSW:
we have also been provided volumes for everything
RS3 FSW GE updates around 4 times a day instead of once
Bulk data pages
The bulk pages are several pages that contain information for every item in GEMW. The pages are what are actually loaded when you use Template:GEP or similar, with the exception of historical items which load the information module.

RSW	OSRSW	Purpose
Module:GEPrices/data.json	osrsw:Module:GEPrices/data.json
osrsw:Module:GEPricesFSW/data	Maps GEMW name to current GE price
Module:GEPricesByIDs/data.json	osrsw:Module:GEPricesByIDs/data.json	Maps item ID to current item price, as an additional shorthand
Module:LastPrices/data.json	osrsw:Module:LastPrices/data.json	Maps GEMW name to the previous GE price
Module:GELimits/data.json	osrsw:Module:GELimits/data.json	Maps GEMW name to GE buying limit
Module:GEValues/data.json	osrsw:Module:GEValues/data.json	Maps GEMW name to item value (which determines alchemy price - every item in the Grand Exchange only)
Module:GEHighAlchs/data.json	osrsw:Module:GEHighAlchs/data.json	Maps GEMW name to item high-level alchemy (GE items only)
Module:GELowAlchs/data.json	osrsw:Module:GELowAlchs/data.json	Maps GEMW name to item low-level alchemy (GE items only)
Module:GEIDs/data.json	osrsw:Module:GEIDs/data.json	Maps GEMW name to item ID (GE items only)
Module:GEVolumes/data.json	osrsw:Module:GEVolumes/data.json	Maps GEMW name to item volume
Module:GEPricesFSW/data.json
Module:LastPricesFSW/data.json
Module:GEVolumesFSW/data.json	osrsw:Module:GEPricesFSW/data
osrsw:Module:LastPricesFSW/data
osrsw:Module:GEVolumesFSW/data	As above but for FSW.
These are standard JSON, you should be able to just load it with anything that can parse JSON. If you visit a the pages normally, they by default display a parsed table of the JSON, but the underlying wikitext is unparsed. Remember, to fetch a page's wikitext only, use action=raw, e.g. https://runescape.wiki/w/Module:GEPrices/data.json?action=raw.

Historical
If you decide to browse the page history of the .json modules, you will find that in early June 2023, they were converted from lua tables to JSON objects. Bear this in mind if you are parsing them, for whatever reason.

The lua tables are in the format ["item"] = value,, with any " in the item name escaped as \" (I don't think there are any examples of this at time of writing, but just in case).

To parse these you can use the regular expression: \["(.*?)"\] = (\d+), where group 1 is the item name and group 2 is the item value or price. You should be able to just iterate the matches. Some of the modules will have the value being 'nil', which is the lua equivalent to null or undefined. This regex will ignore those, but you may need to account for that if you use a split-by-linebreak-and-parse method.

Bulk data API
The bot that updates the above pages (User:Gaz GEBot) makes a dump of the information it uses to generate those pages at:

https://chisel.weirdgloop.org/gazproj/gazbot/rs_dump.json for RS3
https://chisel.weirdgloop.org/gazproj/gazbot/os_dump.json for OSRS
https://chisel.weirdgloop.org/gazproj/gazbot/fsw_dump.json for RS3 Fresh Start Worlds
https://chisel.weirdgloop.org/gazproj/gazbot/osfsw_dump.json for OSRS Fresh Start Worlds
This is true JSON generated every GE bot run. The keys to the object are the item IDs, the values are an object of the data. Additional keys added are %JAGEX_TIMESTAMP%, which is the unix timestamp Jagex reports for the update time, and %UPDATE_DETECTED% is the time the bot detected the update (it checks every 10 minutes).

Feel free to use this over the bulk modules.

Exchange API
The historical data - used to generate the chart - is stored in a separate database under https://api.weirdgloop.org. This is stored by game and then by item ID - you will need to know the item ID of what you are looking up to get the historical prices. The easiest way is to parse the GEIDs bulk page above.

All of the APIs use the same query parameters:

Query parameters
Parameter	Description	Examples
id	separated list of item IDs to fetch. Only the /latest endpoint supports more than one item at a time.	
?id=4151
?id=4151|49430
name	separated list of item names to fetch. These use the exact names as the Exchange pages on the wiki (case sensitive). If both name and id specified, only id is used. Only the /latest supports more than one item at a time.	
?name=Abyssal%20whip
?name=Abyssap%20whip|Chronotes
lang	A language code for use with name. Currently supported are: pt (for the Brazillian-Portuguese RuneScape Wiki) for the RS endpoint, and the default en for both endpoints (lang=en can be omitted).	
?name=Chicote%20abissal&lang=pt
?name=Chicote%20abissal|Cronotas&lang=pt
Endpoints
RS URL	OSRS URL	Description
https://api.weirdgloop.org/exchange/history/rs/all
Examples: https://api.weirdgloop.org/exchange/history/rs/all?id=4151
https://api.weirdgloop.org/exchange/history/rs/all?name=Chronotes	https://api.weirdgloop.org/exchange/history/osrs/all
Examples: https://api.weirdgloop.org/exchange/history/osrs/all?id=4151
https://api.weirdgloop.org/exchange/history/osrs/all?name=Nightmare%20staff	Returns all prices for this item. Used to generate the full charts. Limited to 1 item per request.
https://api.weirdgloop.org/exchange/history/rs/sample
Examples: https://api.weirdgloop.org/exchange/history/rs/sample?id=4151
https://api.weirdgloop.org/exchange/history/rs/sample?name=Chronotes	https://api.weirdgloop.org/exchange/history/osrs/sample
Examples: https://api.weirdgloop.org/exchange/history/osrs/sample?id=4151
https://api.weirdgloop.org/exchange/history/osrs/sample?name=Nightmare%20staff	Returns a sample of prices - 150 prices spread approximately evenly over the entire dataset of the item. Used to generate the small charts in infobox item, where only a relatively small amount of data points are needed to get the general shape of the price graph. Limited to 1 item per request.
https://api.weirdgloop.org/exchange/history/rs/last90d
Examples: https://api.weirdgloop.org/exchange/history/rs/last90d?id=4151
https://api.weirdgloop.org/exchange/history/rs/last90d?name=Chronotes	https://api.weirdgloop.org/exchange/history/osrs/last90d
Examples: https://api.weirdgloop.org/exchange/history/osrs/last90d?id=4151
https://api.weirdgloop.org/exchange/history/osrs/last90d?name=Nightmare%20staff	Returns the last 90 days of prices, as an array of arrays, for a smaller but accurate chart of recent prices. Limited to 1 item per request.
https://api.weirdgloop.org/exchange/history/rs/latest
Examples: https://api.weirdgloop.org/exchange/history/rs/latest?id=4151
https://api.weirdgloop.org/exchange/history/rs/latest?name=Chronotes	https://api.weirdgloop.org/exchange/history/osrs/latest
Examples: https://api.weirdgloop.org/exchange/history/osrs/latest?id=4151
https://api.weirdgloop.org/exchange/history/osrs/latest?name=Nightmare%20staff	Returns the most recent price. Limited to 100 items per request.
Return types
All return types are a JSON object where the keys are the requested IDs/names, and the values are the requested data for the key.

For the former three entry points (/all /sample /last90d) the data is formatted as an array of arrays. Each inner array is either [ UNIXtimestamp, price ], or if volume is available for that timestamp, [ UNIXtimestamp, price, volume ], where all values are numbers.

For the latter entry point (/latest) the data is formatted as a JSON object, with keys id (string), timestamp (string, as an ISO 8601 format datetime), price (number), and volume (number). Volume will be null if not present. Note that calling the latest entry point for a historical item will not return a price (e.g. ID 1119 - Steel platebody (historical).

If all items you request (ID or name) are not present in the database, a JSON object with with success=false and an error message is returned, e.g. {"success":false,"error":"Item(s) not found in the database"}. If at least one of the specified items exists in the database, you will get a return of the data that exists, and the items that do not exist will not be present in the response. (Example: https://api.weirdgloop.org/exchange/history/rs/latest?id=0%7C4151 - requests ID 0 (dwarf remains, not present in the database since it is untradeable) and ID 4151 (abyssal whip, present). A success=false object will also be returned if you request too many items for an endpoint (100 for latest, 1 for the others).

Exchange API for indices
All of these entry points also support accessing the RuneScape:Grand Exchange Market Watch indices too. Simply use the ID as the name of the generator template for the index (obviously, with spaces encoded as %20, though usually your requesting library will do that automatically):

Index	RS entry points	OS entry points
Template:GE Common Trade Index
osrsw:Template:GE Common Trade Index	
https://api.weirdgloop.org/exchange/history/rs/all?id=GE%20Common%20Trade%20Index
https://api.weirdgloop.org/exchange/history/rs/sample?id=GE%20Common%20Trade%20Index
https://api.weirdgloop.org/exchange/history/rs/last90d?id=GE%20Common%20Trade%20Index
https://api.weirdgloop.org/exchange/history/rs/latest?id=GE%20Common%20Trade%20Index

https://api.weirdgloop.org/exchange/history/osrs/all?id=GE%20Common%20Trade%20Index
https://api.weirdgloop.org/exchange/history/osrs/sample?id=GE%20Common%20Trade%20Index
https://api.weirdgloop.org/exchange/history/osrs/last90d?id=GE%20Common%20Trade%20Index
https://api.weirdgloop.org/exchange/history/osrs/latest?id=GE%20Common%20Trade%20Index

Template:GE Food Index
osrsw:Template:GE Food Index	
https://api.weirdgloop.org/exchange/history/rs/all?id=GE%20Food%20Index
https://api.weirdgloop.org/exchange/history/rs/sample?id=GE%20Food%20Index
https://api.weirdgloop.org/exchange/history/rs/last90d?id=GE%20Food%20Index
https://api.weirdgloop.org/exchange/history/rs/latest?id=GE%20Food%20Index

https://api.weirdgloop.org/exchange/history/osrs/all?id=GE%20Food%20Index
https://api.weirdgloop.org/exchange/history/osrs/sample?id=GE%20Food%20Index
https://api.weirdgloop.org/exchange/history/osrs/last90d?id=GE%20Food%20Index
https://api.weirdgloop.org/exchange/history/osrs/latest?id=GE%20Food%20Index

Template:GE Herb Index
osrsw:Template:GE Herb Index	
https://api.weirdgloop.org/exchange/history/rs/all?id=GE%20Herb%20Index
https://api.weirdgloop.org/exchange/history/rs/sample?id=GE%20Herb%20Index
https://api.weirdgloop.org/exchange/history/rs/last90d?id=GE%20Herb%20Index
https://api.weirdgloop.org/exchange/history/rs/latest?id=GE%20Herb%20Index

https://api.weirdgloop.org/exchange/history/osrs/all?id=GE%20Herb%20Index
https://api.weirdgloop.org/exchange/history/osrs/sample?id=GE%20Herb%20Index
https://api.weirdgloop.org/exchange/history/osrs/last90d?id=GE%20Herb%20Index
https://api.weirdgloop.org/exchange/history/osrs/latest?id=GE%20Herb%20Index

Template:GE Log Index
osrsw:Template:GE Log Index	
https://api.weirdgloop.org/exchange/history/rs/all?id=GE%20Log%20Index
https://api.weirdgloop.org/exchange/history/rs/sample?id=GE%20Log%20Index
https://api.weirdgloop.org/exchange/history/rs/last90d?id=GE%20Log%20Index
https://api.weirdgloop.org/exchange/history/rs/latest?id=GE%20Log%20Index

https://api.weirdgloop.org/exchange/history/osrs/all?id=GE%20Log%20Index
https://api.weirdgloop.org/exchange/history/osrs/sample?id=sample?id=GE%20Log%20Index
https://api.weirdgloop.org/exchange/history/osrs/last90d?id=GE%20Log%20Index
https://api.weirdgloop.org/exchange/history/osrs/latest?id=GE%20Log%20Index

Template:GE Metal Index
osrsw:Template:GE Metal Index	
https://api.weirdgloop.org/exchange/history/rs/all?id=GE%20Metal%20Index
https://api.weirdgloop.org/exchange/history/rs/sample?id=GE%20Metal%20Index
https://api.weirdgloop.org/exchange/history/rs/last90d?id=GE%20Metal%20Index
https://api.weirdgloop.org/exchange/history/rs/latest?id=GE%20Metal%20Index

https://api.weirdgloop.org/exchange/history/osrs/all?id=GE%20Metal%20Index
https://api.weirdgloop.org/exchange/history/osrs/sample?id=GE%20Metal%20Index
https://api.weirdgloop.org/exchange/history/osrs/last90d?id=GE%20Metal%20Index
https://api.weirdgloop.org/exchange/history/osrs/latest?id=GE%20Metal%20Index

Template:GE Rune Index
osrsw:Template:GE Rune Index	
https://api.weirdgloop.org/exchange/history/rs/all?id=GE%20Rune%20Index
https://api.weirdgloop.org/exchange/history/rs/sample?id=GE%20Rune%20Index
https://api.weirdgloop.org/exchange/history/rs/last90d?id=GE%20Rune%20Index
https://api.weirdgloop.org/exchange/history/rs/latest?id=GE%20Rune%20Index

https://api.weirdgloop.org/exchange/history/osrs/all?id=GE%20Rune%20Index
https://api.weirdgloop.org/exchange/history/osrs/sample?id=GE%20Rune%20Index
https://api.weirdgloop.org/exchange/history/osrs/last90d?id=GE%20Rune%20Index
https://api.weirdgloop.org/exchange/history/osrs/latest?id=GE%20Rune%20Index

Template:GE Discontinued Rare Index	
https://api.weirdgloop.org/exchange/history/rs/all?id=GE%20Discontinued%20Rare%20Index
https://api.weirdgloop.org/exchange/history/rs/sample?id=GE%20Discontinued%20Rare%20Index
https://api.weirdgloop.org/exchange/history/rs/last90d?id=GE%20Discontinued%20Rare%20Index
https://api.weirdgloop.org/exchange/history/rs/latest?id=GE%20Discontinued%20Rare%20Index

OSRS doesn't have a discontinued rare index
Be aware that with indices, the ID returned by latest is a string, and the majority of prices are floating points. The timestamp may not line up with the GE update timestamp, since the indices are updated right after a full GE update, using the current time, whereas GEMW items are updated with the timestamp Jagex returns.

A note on volumes
Volumes on RSW have two forms depending on time:

Prior to 14 December 2022, volume is only available for the top 100 most traded items, and is a rolling 7-day window.
From 15 December 2022 onwards, volume is available for every item (except bonds), on a daily basis.
In OSRSW (and both FSW data sets), volumes are available for almost every item as daily volume (again, excluding bonds).

Note that "daily volume" is really the total volume in the previous GE update period, which may be longer or shorter than 24 hours.

Using GEMW in wiki modules
Modules are lua code that runs as a layer above wikitext. Modules are generally easier to program complex things in, as lua is an actual programming language with proper variables, datatypes, loops, etc, instead of wikitext.

This guide assumes basic understanding of lua and how to use that on the wiki - see Help:Editing/Lua for more information.

Loading prices
As prices are stored in lua, there are a few ways to load them in to a module.

Recommended - Module:ExchangeLite
ExchangeLite is the recommended way to obtain GE prices. Usage is simple.

First load in the module local exg = require('Module:ExchangeLite')
When a price is required, it can be loaded using the exg.price(item) function, e.g. exg.price('Bones'). (This is syntactic sugar for #3).
For any other field from the information module, use exg.load({args = { item, field }}), e.g. exg.load({args = { 'Bones', 'limit' }})
The primary downsides of the module are that there are no error checks and no title redirects, so you will have to implement those yourself if that is necessary. See example for error checking.

Recommended - Module:Exchange
Exchange is the full module for loaded GE prices. As it is larger, it is not recommended for larger, more complex modules that use a lot of GE prices as it can cause additional computation time and memory usage over ExchangeLite.

The important functions are (given local exg = require('Module:Exchange')):

exg._price(item, multiplier, format, round) - item to get the price of, multiplier for the price, format the result with commas, round the result to X decimal places
exg._limit(item) - GE limit for item
exg._value(item) - internal value for item
exg._diff(item) - price difference for item
exg._exists(item) - does the exchange module for the page exist
The Exchange module does have error protection and title redirects, unlike ExchangeLite.

Situational - loading the bulk modules directly
Loading the bulk modules directly can save a little overhead of using one of the middleman exchange modules, but you will have to make sure you handle missing prices yourself.

local prices = mw.loadJsonData('Module:GEPrices/data.json')

function gep(item)
    local pr = prices[item]
    if pr then
        return pr
    end
    return 0
end

function example()
    return gep('Decorated farming urn (nr)') - gep('Soft clay') * 2
end
Wiki javascript
Wiki javascript usage of prices is essentially a subset of external usage, but with some additional advantages of being directly on the wiki. This covers methods that can only be used by being on the wiki.

Making the wiki do the loading
The easiest way to load prices with wiki javascript is to not load them with wiki javascript at all! It is simpler to, on the page the script is running on, have the wiki load the price and put it in a containing tag that can be found easily.

e.g. one could use <span class="example-calc-info" data-name="Bones" data-price="{{GEP|Bones}}">{{Coins|{{GEP|Bones}}}}</span>. This can easily be selected via $('span.example-calc-info').attr('data-price').

This can apply for both scripts that run on one page and scripts that run on large numbers of pages.

An example of this is Module:Archaeology calculator materials data (used on Calculator:Archaeology/Restoring artefacts), which loads a collection of prices and dumps them on the page for the calculator to load.

Javascript loading
If you need the javascript to load the information and can't put it into a wiki page, then there's a few ways to load the data. All the methods from #External usage also apply.

Two example usages, to get and parse current data:

// jQuery - though any standard GET request with JSON.parse will also be fine
$.getJSON('https://api.weirdgloop.org/exchange/history/rs/latest?id=4151').done(function(res, jqxhr){
    console.log(res);
});
External usage
Again, there are several ways to fetch a price, many of which are shared by the on-wiki javascript section. This should work in any language (you'll need some sort of requests library, as well as regular expressions or JSON/XML parsing), but I'll be giving examples in javascript.

Using the API
The API is the simplest and most recommended way to get prices. Just send a GET request to the endpoints described in #Exchange API above. This returns a JSON object containing prices.

The only thing we ask you to do is set a descriptive user-agent when querying our APIs. See RuneScape:APIs for more information.

Google Sheets
We have written a script for use with Google Sheets to get exchange prices. The script is here: User:Gaz Lloyd/RSW Exchange API for Google Sheets.js

Open a Google Sheet
Tools -> Script editor...
Replace the default empty function with this script (if you already have some other custom functions, probably best to make a new file)
Save the script (top bar icon, or ctrl-s)
Name the project (name it whatever, it doesn't matter)
The functions are now working in the spreadsheet
If you reload the spreadsheet, the 'Exchange' menu will appear at the top of the page (next to Help).
You can find some basic usage instructions and a function that generates a demo there
Using either of these will trigger an authorisation request, as it involves modifying the spreadsheet
The functions should work fine without it
We are making this a full Google Sheets add-on, so stay tuned!

Querying the wiki
Bulk pages
If you want the current prices, or other bulk data for a lot of items, you can request one of the bulk pages (see #Bulk data pages above) from the wiki by using action=raw, e.g. https://runescape.wiki/w/Module:GEPrices/data?action=raw. This will return the raw wikitext of the page, which you can change from the lua-format tables into your choice of data structure.

You should probably only use the bulk prices module (GEPrices) if you are looking for prices of every item. The API is likely a better fit for most uses.

Other queries
You should avoid querying the wiki for Exchange prices or volumes. The API will always be a better thing to use.

If you are looking for non-price/non-volume information, you can fetch the data using the MediaWiki API: https://runescape.wiki/api.php / https://oldschool.runescape.wiki/api.php. You are likely looking for action=query or action=parse.

Generally, if you can't find what you're looking for, come contact us and we're likely to know how to get what you're looking for. We're always happy to help!

Navigation menu
04:14
Not logged in
Talk
Contributions
Sign up
Log in
Project pageDiscussion
Edit sourceHistory

More
Search the RuneScape Wiki
Search
Discord
Navigation
About us
User help
Random page
Recent changes 
Ritual of the Mahjarrat/Quick guide
14m ago - Valkyrie7

Arcane blast neck
26m ago - 2471btw

Princess Pancake
55m ago - Coelacanth0794

Prince Hernando
55m ago - Coelacanth0794

Show more...
Guides
List of quests
Skill training
Money making
Treasure Trails
New player guide
Databases
PvM portal
Calculators
Bestiary
Achievements
Editing
How to edit
Policies
Projects
New files
The Wikian
Community
Discussions
Clan Chat
More RuneScape
OSRS Wiki
RSC Wiki
Wiki Trivia Games
RuneScape.com
Tools
What links here
Page information
Browse properties
Make new page
Report Ad
This page was last modified on 15 June 2023, at 12:44.
Content on this site is licensed under CC BY-NC-SA 3.0; additional terms apply.
RuneScape and RuneScape Old School are the trademarks of Jagex Limited and are used with the permission of Jagex.


User:Gaz Lloyd/RSW Exchange API for Google Sheets.js
< User:Gaz Lloyd
After saving, you may need to bypass your browser's cache to see the changes. For further information, see Wikipedia:Bypass your cache.

In most Windows and Linux browsers: Hold down Ctrl and press F5.
In Safari: Hold down ⇧ Shift and click the Reload button.
In Chrome and Firefox for Mac: Hold down both ⌘ Cmd+⇧ Shift and press R.
/*******
 * RSW Exchange API integration for Google Sheets.
 * 
 * Provides the following functions to fetch GE market prices from the wiki's API:
 * * getRSPrice
 * * getOSRSPrice
 * 
 * See https://api.weirdgloop.org or https://runescape.wiki/w/User:Gaz_Lloyd/using_gemw for more information on the exchange API.
 * 
 * If you are looking for a script for the realtime prices for OSRS (as provided by RuneLite), see https://oldschool.runescape.wiki/w/User:Gaz_Lloyd/OSRSW_realtime_API_for_Google_Sheets.js
 * 
 * Usage:
 * 1) Open a Google Sheet
 * 2) Tools -> Script editor...
 * 3) Replace the default empty function with this script (if you already have some other custom functions, probably best to make a new file)
 * 4) Save the script (top bar icon, or ctrl-s)
 * 5) Name the project (name it whatever, it doesn't matter)
 * 6) The functions are now working in the spreadsheet
 * 7) If you reload the spreadsheet, the 'Exchange' menu will appear at the top of the page (next to Help)
 *    You can find some basic usage instructions and a function that generates a demo there
 *    Using either of these will trigger an authorisation request, as it involves modifying the spreadsheet
 *    The functions should work fine without it
 * 
 * @example
 * An example spreadsheet using the functions is here: https://docs.google.com/spreadsheets/d/1mn8bpaOXZsQ5xjM34jtYeyNgiyGkfmLkdJmFKhayBv0/edit#gid=0
 * 
 * 
 * 
 * Feel free to contact me if you have any questions:
 * <gaz[at]weirdgloop.org>
 * <@Gaz_Lloyd> on Twitter
 * <@Gaz#7521> on Discord - or #wiki-tech in the wiki's server discord.gg/runescapewiki
 * <User:Gaz Lloyd> on the RuneScape Wiki
 * <Gaz Lloyd> or <Gaz L> in-game
 * 
 * @author Gaz Lloyd
 */
const index_map = {
  'GE Food Index': true,
  'GE Metal Index': true,
  'GE Logs Index': true,
  'GE Herb Index': true,
  'GE Rune Index': true,
  'GE Common Trade Index': true,
  'GE Discontinued Rare Index': true
};
const MAX_PER_REQ = 100;

/**
 * Determines what type of array we're dealing with, if any
 *
 * @param {*} a - The thing to check (can be any type)
 * @return {String} - The array type, from: 3d, 1d, 1x1, 1xN, Nx1, NxN, or notarray
 */
function _getArrayType(a) {
  var rows=0, cols=0, has_more=false;
  if (Array.isArray(a)) {
    rows = a.length;
    if (rows > 0 && Array.isArray(a[0])) {
      cols = a[0].length;
      if (cols > 0 && Array.isArray(a[0][0])) {
        has_more=true;
      }
    }
  }
  if (has_more) return "3d"; //3D or higher array
  if (rows > 0 && cols === 0) return "1d"; // single dimension
  if (rows === 1 && cols === 1) return "1x1"; //2d array with 1 row and 1 col
  if (rows === 1 && cols > 1) return "1xN"; // single row
  if (rows > 1 && cols === 1) return "Nx1"; // single column
  if (rows > 1 && cols > 1) return "NxN"; //multiple rows and columns
  return "notarray"; //not an array
}


/**
 * Gets the price for the input RS3 item ID or name or array of either
 *
 * @param {Array<Array<<*>>} id - The RS3 item ID, item name, or array of either (or mix) to fetch the price(s) for
 * @param {Boolean} [allowNoPrice=false] - If true, errors with parsing or fetching the price will return as 0 instead of erroring
 * @return {Array<Array<<Number>>} - The price(s)
 * @customfunction
 */
function getRSPrice(items, allowNoPrice) {
  return _fetchprices("https://api.weirdgloop.org/exchange/history/rs/latest", items, allowNoPrice);
}

/**
 * Gets the price for the input OSRS item ID or name or array of either
 *
 * @param {Array<Array<<*>>} items - The OSRS item ID, item name, or array of either (or mix) to fetch the price(s) for
 * @param {Boolean} [allowNoPrice=true] - If true, errors with parsing or fetching the price will return as 0 instead of erroring
 * @return {Array<Array<<Number>>} - The price(s)
 * @customfunction
 */
function getOSRSPrice(items, allowNoPrice) {
  return _fetchprices("https://api.weirdgloop.org/exchange/history/osrs/latest", items, allowNoPrice);
}

/**
 * Gets the prices for the list of items, batching as necessary.
 *
 * @param {String} param - URL parameter to use
 * @param {Array} items - List of things to get
 * @return {Object} - The JSON response from the url
 */
function _getAndMerge(url, param, items) {
  var ret = {};
  var offset = 0;
  while (offset < items.length) {
    var new_offset = Math.min(offset + MAX_PER_REQ, items.length);
    var requrl = url + '?' + param + '=' + encodeURIComponent(items.slice(offset, new_offset).join('|'));
    var resp = UrlFetchApp.fetch(requrl);
    var data = JSON.parse(resp.getContentText());
    Object.assign(ret, data);
    offset = new_offset;
  }
  return ret;
}

/**
 * Does the actual work of fetching prices
 * 
 * The query URL is constructed by concatenating url_prefix, the id, url_postfix.
 *
 * @param {String} url - The url to query
 * @param {Array<Array<<*>>} items - The item ID, item name, or array of either (or mix) to fetch the price(s) for
 * @param {Boolean} [allowNoPrice=true] - If true, errors with parsing or fetching the price will return as 0 instead of erroring
 * @return {Array<Array<<Number>>} - The price(s)
 */
function _fetchprices(url, _items, allowNoPrice) {
  allowNoPrice = allowNoPrice === true;
  var ids_type = _getArrayType(_items);
  var items;
  
  // normalise items into a standard [[r1c1, r1c2, ...], [r2c1, r2c2, ...], ...] style array
  if (ids_type === '3d') {
    throw 'Invalid data type';
  }
  else if (ids_type === 'notarray') {
    // just a raw value
    // make into a 1x1 array
    items = [[_items]];
  }
  else if (ids_type === '1d') {
    // just a row
    // encapsulate into another array
    items = [_items];
  }
  else {
    // already in the right format
    // 1x1, Nx1, 1xN, NxN
    items = _items;
  }
  
  var ids_to_query = [], names_to_query = [];
  for (var r=0; r<items.length; r++) {
    for (var c=0; c<items[r].length; c++) {
      var item = items[r][c];
      if (index_map.hasOwnProperty(item)) {
        if (!ids_to_query.includes(items)) {
          ids_to_query.push(item);
        }
      } else if (typeof(item) === 'string' && item.match(/^\d+$/) === null) {
        if (!names_to_query.includes(item)) {
          names_to_query.push(item);
        }
      } else {
        if (!ids_to_query.includes(item)) {
          ids_to_query.push(item);
        }
      }
    }
  }
  
  var all_data = {};
  Object.assign(all_data, _getAndMerge(url, 'id', ids_to_query));
  Object.assign(all_data, _getAndMerge(url, 'name', names_to_query));
  var ret = [];
  for (var r=0; r<items.length; r++) {
    var row = [];
    for (var c=0; c<items[r].length; c++) {
      var item = items[r][c];
      if (all_data.hasOwnProperty(item)) {
        row.push(all_data[item].price);
      } else {
        if (allowNoPrice) {
          row.push(0);
        } else {
          throw 'Item "'+item+'" not found'
        }
      }
    }
    ret.push(row);
  }
  return ret;
}


/**
 * Runs automatically when the script loads
 * 
 */
function onOpen() {
  var spreadsheet = SpreadsheetApp.getActive();
  var menuItems = [
    {name:'Usage instructions', functionName: 'usageInstructions'},
    {name:'Generate demo', functionName: 'generateDemo'}
  ];
  spreadsheet.addMenu('Exchange', menuItems);
}

function usageInstructions() {
  Browser.msgBox("RSW Exchange API Google Sheets add-on usage information", "This add-on provides two main functions that easily allow you to fetch GE prices from the RuneScape Wiki's Exchange API.\ngetRSPrice(items) and getOSRSPrice(items), which will fetch the price for the specified item(s) from the API for RuneScape or Oldschool RuneScape, respectively.\n\nYou can specify a single item, or an array of items. Items can be the item ID, or the item name (make sure to spell it correctly, case sensitive, exactly as the wiki names the Exchange page), or a mix.\n\nIf the price is not found, an error is thrown. By setting the second parameter to TRUE, 0 is returned for missing prices instead - getRSPrices(items, TRUE), getOSRSPrices(items, TRUE)\n\nYou can specify an array of items with one formula, and the output will be formatted in the same shape as the input. Use the 'Generate Demo' menu option for an example.", Browser.Buttons.OK);
}

function generateDemo() {
  var spsh = SpreadsheetApp.getActiveSpreadsheet();
  var sheetname_base = 'Exchange API demo';
  var sheetname_working = sheetname_base;
  var disamb = 0;
  while (spsh.getSheetByName(sheetname_working) !== null)  {
    disamb++;
    sheetname_working = sheetname_base + ' (' + disamb + ')';
  }
  var newSheet = spsh.insertSheet(sheetname_working);
  newSheet
    .appendRow(['RSW Exchange API demonstration'])
    .appendRow([' '])
    .appendRow(['Looking up a single price'])
    .appendRow(['Item', 'RS price', 'OSRS price', 'Comment'])
    .appendRow(['Abyssal whip', '=getRSPrice(A5)', '=getOSRSPrice(A5)'])
    .appendRow(['Fire rune', '=getRSPrice(A6)', '=getOSRSPrice(A6)'])
    .appendRow(['526', '=getRSPrice(A7)', '=getOSRSPrice(A7)', 'Can also look up IDs (526 = Bones)'])
    .appendRow([' '])
    .appendRow(['Lookup up multiple prices with one function'])
    .appendRow(['Items', 'RS price', 'OSRS price', 'Comment'])
    .appendRow(['Air rune'])
    .appendRow(['Earth rune'])
    .appendRow(['Nature rune'])
    .appendRow(['Soul rune']);
  newSheet.getRange('B11').setFormula('=getRSPrice(A11:A14)');
  newSheet.getRange('C11').setFormula('=getOSRSPrice(A11:A14)');
  newSheet.getRange('D11').setValue('<- Notice that the formula is only in these cells, and it fills down in the same shape');
  newSheet
    .appendRow([' '])
    .appendRow(['Weird shape example - the formula returns in the same shape'])
    .appendRow(['Steel platebody', 'Rune platebody'])
    .appendRow(['Steel platelegs', 'Rune platelegs']);
  newSheet.getRange('C17').setFormula('=getRSPrice(A17:B18)')
  newSheet
    .appendRow([' '])
    .appendRow(['Errors'])
    .appendRow(['If you ask for an invalid item, an error is thrown'])
    .appendRow(['Green eggs and ham', '=getRSPrice(A22)', '=getOSRSPrice(A22)'])
    .appendRow(['By setting the second parameter to TRUE, 0 is returned instead'])
    .appendRow(['Green eggs and ham', '=getRSPrice(A24, TRUE)', '=getOSRSPrice(A24, TRUE)'])
    .appendRow(['This also applies to arrays'])
    .appendRow(['Big bones'])
    .appendRow(['Frost dragon bones'])
    .appendRow(['Superior dragon bones']);
  newSheet.getRange('B26').setFormula('=getRSPrice(A26:A28, TRUE)');
  newSheet.getRange('C26').setFormula('=getOSRSPrice(A26:A28, TRUE)');
  newSheet.setColumnWidth(1, 180);
  var boldCells = [ 'A1', 'A3:D4', 'A9:D10', 'A16', 'A20' ];
  for (var i=0; i<boldCells.length; i++) {
    newSheet.getRange(boldCells[i]).setFontWeight('bold');
  }
  SpreadsheetApp.flush();
}
Navigation menu
04:15
Not logged in
Talk
Contributions
Sign up
Log in
User pageDiscussionContributions
View sourceHistory

More
Search the RuneScape Wiki
Search
Discord
Navigation
About us
User help
Random page
Recent changes 
Ritual of the Mahjarrat/Quick guide
15m ago - Valkyrie7

Arcane blast neck
27m ago - 2471btw

Princess Pancake
56m ago - Coelacanth0794

Prince Hernando
56m ago - Coelacanth0794

Show more...
Guides
List of quests
Skill training
Money making
Treasure Trails
New player guide
Databases
PvM portal
Calculators
Bestiary
Achievements
Editing
How to edit
Policies
Projects
New files
The Wikian
Community
Discussions
Clan Chat
More RuneScape
OSRS Wiki
RSC Wiki
Wiki Trivia Games
RuneScape.com
Tools
What links here
User contributions
Logs
View user groups
Page information
Make new page
Report Ad
This page was last modified on 22 July 2021, at 01:19.
Content on this site is licensed under CC BY-NC-SA 3.0; additional terms apply.
RuneScape and RuneScape Old School are the trademarks of Jagex Limited and are used with the permission of Jagex.

Help:APIs
This article is about the wiki's APIs. For RuneScape's APIs, see Application programming interface.
The RuneScape Wikis host a variety of APIs that are available for consumption by non-wiki projects. These include:

The MediaWiki API to read content and query against the wiki's MediaWiki database. See here for detailed documentation.
The Ask API to read Semantic MediaWiki property values. See here for detailed documentation, also see Special:Browse to view all available properties for a page.
APIs to read Grand Exchange prices/history, and more. Official documentation is available on https://api.weirdgloop.org. See here for details and examples, including Google Sheets integration.
The current Voice of Seren, Travelling Merchant's Shop stock and the latest RuneScape news are also available via APIs on https://api.weirdgloop.org.
Coming soon:
The best rune combinations for Rune Goldberg Machine via https://api.weirdgloop.org.
The status of the Kharid-et pylon and the Rex skeleton (Osseous) via https://api.weirdgloop.org.
Acceptable use policy
Within reason, we want people to use these APIs as much as they need to build cool projects and tools. We do not explicitly ratelimit any of the endpoints, and we do our best to cache the responses at multiple levels.

However, we reserve the right to limit access to anyone, if their usage is so frequent that it threatens the stability of the entire API. We don't know where that line is right now, but for Grand Exchange prices, it would probably have to be double-digit queries per second for a sustained period. If we end up blocking your tool, feel free to reach out on Discord and we'll see if there's a better solution for what you're doing.

Please set a descriptive User-Agent!
This is the only thing we ask! If you're using automated tooling to scrape the wiki's APIs, please set a User-Agent that describes what you're using it for: for example, dps-calculator-stats-scraper. This helps us understand what people are using the APIs for, and helps us reach out in advance if there are any breaking changes coming.

We currently pre-emptively block the following user-agents, and may add more:

python-requests
ApacheHttpClient
Note this does not at all mean you can't use the python-requests library or similar, but we just ask that you set a user-agent in your code.

Category: RuneScape Wiki community