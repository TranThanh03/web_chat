import { setAccessToken } from '@/utils/axiosInstance';
import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';

export interface AuthContextType {
    authenticated: boolean;
    login: (token: string) => void;
    logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

interface Props {
    children: ReactNode;
}

export const AuthProvider = ({ children }: Props) => {
    const [authenticated, setAuthenticated] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    const login = (token: string) => {
        setAccessToken(token);
        setAuthenticated(true);
    };

    const logout = () => {
        setAccessToken(null);
        setAuthenticated(false);
    };

    const fetchRefreshToken = useCallback(async () => {
        
    }, []);

    useEffect(() => {
        const init = async () => {
            await fetchRefreshToken();
            setIsLoading(false);
        };

        init();
    }, [fetchRefreshToken]);

    const value = useMemo(
        () => ({
            authenticated,
            login,
            logout
        }),
        [authenticated]
    );

    if (isLoading) return null;

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
