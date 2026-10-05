// Copyright (C) 2017-2023 Smart code 203358507

const routes = require('stremio/routes');
const { routesRegexp } = require('stremio/common');

const routerViewsConfig = [
    [
        {
            ...routesRegexp.board,
            component: routes.Board
        }
    ],
    [
        {
            ...routesRegexp.intro,
            component: routes.Intro
        },
        {
            ...routesRegexp.discover,
            component: routes.Discover
        },
        {
            ...routesRegexp.library,
            component: routes.Library
        },
        {
            ...routesRegexp.calendar,
            component: routes.Calendar
        },
        {
            ...routesRegexp.continuewatching,
            component: routes.Library
        },
        {
            ...routesRegexp.search,
            component: routes.Search
        }
    ],
    [
        {
            ...routesRegexp.metadetails,
            component: routes.MetaDetails
        }
    ],
    [
        // Addon management is intentionally not exposed by MW Play.
        // Installed/synchronized addons remain available to the core, while
        // customers cannot browse, install or uninstall them from this client.
        {
            ...routesRegexp.settings,
            component: routes.Settings
        }
    ],
    [
        {
            ...routesRegexp.player,
            component: routes.Player
        }
    ]
];

module.exports = routerViewsConfig;
