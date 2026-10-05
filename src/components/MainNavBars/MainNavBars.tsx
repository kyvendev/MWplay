// Copyright (C) 2017-2023 Smart code 203358507

import React, { memo } from 'react';
import classnames from 'classnames';
import { VerticalNavBar, HorizontalNavBar } from 'stremio/components/NavBar';
import styles from './MainNavBars.less';

// MW Play keeps content-source management outside the customer-facing client.
const TABS = [
    { id: 'board', label: 'Início', icon: 'home', href: '#/' },
    { id: 'discover', label: 'Explorar', icon: 'discover', href: '#/discover' },
    { id: 'library', label: 'Minha Lista', icon: 'library', href: '#/library' },
    { id: 'calendar', label: 'Calendário', icon: 'calendar', href: '#/calendar' },
];

type Props = {
    className: string,
    route?: string,
    query?: string,
    children?: React.ReactNode,
};

const MainNavBars = memo(({ className, route, query, children }: Props) => {
    return (
        <div className={classnames(className, styles['main-nav-bars-container'], 'mw-play-shell')}>
            <HorizontalNavBar
                className={styles['horizontal-nav-bar']}
                route={route}
                query={query}
                backButton={false}
                searchBar={true}
                fullscreenButton={true}
                navMenu={true}
            />
            <VerticalNavBar className={styles['vertical-nav-bar']} selected={route} tabs={TABS} />
            <div className={styles['nav-content-container']}>{children}</div>
        </div>
    );
});

export default MainNavBars;
