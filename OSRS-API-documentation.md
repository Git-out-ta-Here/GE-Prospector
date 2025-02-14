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