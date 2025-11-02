export const useSocialLogin = () => {
    const clientId = import.meta.env.VITE_GG_CLIENT_ID;
    const authUrl = import.meta.env.VITE_GG_AUTH_URI;
    const redirectUrl = import.meta.env.VITE_GG_REDIRECT_URI;

    const targetUrl = `${authUrl}?redirect_uri=${encodeURIComponent(redirectUrl)}&response_type=code&client_id=${clientId}&scope=openid%20email%20profile`;
    window.location.href = targetUrl;
}