A simple Forge mod that adds two commands (/tempban and /tempban-ip) that work exactly like the /ban and /ban-ip command, with the difference that you can specify the length of the ban in hours, days and months.

This makes use of Vanilla's 'expired' field in a ban entry which isn't used in the normal ban command.

 

Usage: 
 
    /tempban <selector> <months> <days> <hours> [<reason>]

    /tempban-ip <ip> <months> <days> <hours> [<reason>]
To unban temporarily banned users, using the vanilla /pardon and /pardon-ip commands should do the trick.

Since the 'expired' field isn't used anywhere in vanilla, there's not a great deal of support for it within the code, and users which were banned temporarily may face minor issues when joining a server (for example not being able to connect sometimes due to an exception). If any more severe issues occur, feel free to submit an issue here on GitHub.

This mod is server side only (meaning that players don't need to install this mod at all).
