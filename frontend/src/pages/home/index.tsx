import { memo } from "react";
import HeroSection from "@/components/home/HeroSection";
import Header from "@/components/home/Header";
import Footer from "@/components/home/Footer";
import { DialogProvider } from "@/providers/DialogProvider";
import LoginDialog from "@/components/dialog/LoginDialog";
import SignupDialog from "@/components/dialog/SignupDialog";
import ResetPasswordDialog from "@/components/dialog/ResetPasswordDialog";

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