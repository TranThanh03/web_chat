import ReCAPTCHA from "react-google-recaptcha";
import { useRef, forwardRef, useImperativeHandle, memo } from "react";

export interface RecaptchaCbProps {
    token: string | null;
}

export interface RecaptchaRef {
    executeAsync: () => Promise<string | null>;
}

const RecaptchaInv = forwardRef<RecaptchaRef>((_, ref) => {
    const recaptchaRef = useRef<ReCAPTCHA | null>(null);

    useImperativeHandle(ref, () => ({
        async executeAsync() {
            try {
                if (recaptchaRef.current) {
                    const token = await recaptchaRef.current.executeAsync();
                    recaptchaRef.current.reset();
                    return token;
                }
                return null;
            } catch (error) {
                console.error("reCAPTCHA error:", error);
                return null;
            }
        }
    }));

    return (
        <ReCAPTCHA
            ref={recaptchaRef}
            sitekey={import.meta.env.VITE_RECAPTCHA_INV_SITE_KEY}
            size="invisible"
            badge="bottomright"
            hl="en"
        />
    );
});

export default memo(RecaptchaInv);