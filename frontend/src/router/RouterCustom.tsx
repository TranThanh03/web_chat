import { createBrowserRouter, RouterProvider, Navigate } from "react-router-dom";
import * as page from "@/pages";
import { AuthProvider } from "@/providers/AuthProvider";
import { ROUTERS } from "./routers";

const router = createBrowserRouter([
    {
        element: (
            <AuthProvider>
                <page.Layout />
            </AuthProvider>
        ),
        children: [
            {
                path: ROUTERS.HOME_PAGE,
                element: <page.HomePage />
            },
            {
                path: ROUTERS.AUTHENTICATE_PAGE,
                element: <page.AuthenticatePage />
            },
            {
                path: ROUTERS.ERROR404_PAGE,
                element: <page.Page404 />
            },
            {
                path: ROUTERS.ERROR500_PAGE,
                element: <page.Page500 />
            },
            {
                path: "*",
                element: <Navigate to={ROUTERS.ERROR404_PAGE} replace />
            }
        ]
    }
]);

export default function RouterCustom() {
    return <RouterProvider router={router} />
}
