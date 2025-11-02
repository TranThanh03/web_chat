import NavigationSidebar from '@/components/chat/NavigationSidebar';
import MobileBottomNav from '@/components/mobile/MobileBottomNav';
import { memo } from 'react';
import { Outlet, useLocation } from 'react-router-dom';

const Layout = () => {
    const location = useLocation();
    const path = location.pathname;

    const normalizedPath = path.replace(/\/+$/, "") || "/";
    const isHomePage = normalizedPath === ("/") || normalizedPath.includes("/authenticate") || normalizedPath.includes("/error");

    return (
        isHomePage ? (
            <Outlet />
        ) : (
            <div className="h-screen flex overflow-hidden bg-background">
                <NavigationSidebar />
                <div className="flex-1 flex flex-col min-h-0">
                    <Outlet />
                </div>
                <MobileBottomNav />
            </div>
        )
    )
};

export default memo(Layout);