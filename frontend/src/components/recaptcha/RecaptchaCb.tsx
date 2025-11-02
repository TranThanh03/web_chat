import { forwardRef, memo } from "react";
import ReCAPTCHA from "react-google-recaptcha";

export interface RecaptchaCbProps {
    onChange: (token: string | null) => void,
    tabIndex: number | 0;
}

const RecaptchaCb = forwardRef<ReCAPTCHA, RecaptchaCbProps>(
    ({ onChange, tabIndex }, ref) => {
        return (
            <ReCAPTCHA
                ref={ref}
                sitekey={import.meta.env.VITE_RECAPTCHA_CB_SITE_KEY}
                onChange={onChange}
                tabIndex={tabIndex}
            />
        );
    }
);

export default memo(RecaptchaCb);