import { memo } from "react";
import HeroSection from "@/components/HeroSection";
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { DialogProvider } from "@/providers/DialogProvider";
import LoginDialog from "@/components/dialogs/LoginDialog";
import SignupDialog from "@/components/dialogs/SignupDialog";
import ResetPasswordDialog from "@/components/dialogs/ResetPasswordDialog";

const HomePage = () => {
    return (
        <DialogProvider>
            <div className="min-h-screen flex flex-col">
                <Header />
                <main className="grow">
                    <HeroSection />
                </main>
                <Footer />
            
                <LoginDialog />
                <SignupDialog />
                <ResetPasswordDialog />
            </div>
        </DialogProvider>
    );
};

export default memo(HomePage);