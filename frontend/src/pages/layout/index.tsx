import { memo } from 'react';
import { Outlet, useLocation } from 'react-router-dom';

const Layout = () => {
    const location = useLocation();
    const path = location.pathname;

    const isValidPath = !path.includes("/auth") && !path.includes("/error") && !path.includes("/customers/activate");

    return (
        <Outlet />
    );
};

export default memo(Layout);