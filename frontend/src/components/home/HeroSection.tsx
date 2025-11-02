import { MessageSquare, Upload, Users, Zap } from "lucide-react";
import { memo } from "react";

const HeroSection = () => {
    const features = [
        {
            icon: MessageSquare,
            title: "Real-time Messaging",
            description: "Send and receive messages instantly with no delays. Experience seamless communication with real-time delivery and read receipts."
        },
        {
            icon: Upload,
            title: "File Sharing",
            description: "Share documents, images, and files of any type with ease. Support for multiple file formats with secure cloud storage."
        },
        {
            icon: Users,
            title: "Group Conversations",
            description: "Create and manage group chats effortlessly. Add unlimited members, assign roles, and organize your team communications."
        },
        {
            icon: Zap,
            title: "Lightning Fast",
            description: "Enjoy smooth, lag-free performance with optimized infrastructure. Built for speed and reliability across all devices."
        }
    ];

    return (
        <div className="pt-4 sm:pt-12">
        <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
            <div className="text-center mb-16 sm:mb-20">
            <h2 className="text-4xl sm:text-5xl lg:text-6xl font-bold mb-4 sm:mb-6 leading-tight">
                Connect everyone,{" "}
                <span className="bg-linear-to-r from-primary via-primary/80 to-primary/60 bg-clip-text text-transparent">
                anytime, anywhere
                </span>
            </h2>
            <p className="text-base sm:text-lg lg:text-xl text-muted-foreground max-w-3xl mx-auto mb-8 sm:mb-10 leading-relaxed">
                Experience the future of real-time communication with our modern chat platform. 
                Featuring intuitive design, powerful collaboration tools, and enterprise-grade security. 
                Built for teams, loved by millions worldwide.
            </p>
            
            <div className="relative max-w-4xl mx-auto mb-12 sm:mb-16">
                <div className="relative rounded-2xl overflow-hidden shadow-2xl border border-border bg-linear-to-r from-primary/5 to-accent/5 p-8 sm:p-12">
                    <div className="relative rounded-xl bg-linear-to-br from-primary/10 via-primary/5 to-accent/10 flex items-center justify-center">
                        <img
                            src="/slide.png"
                            alt="slide"
                            className="w-full rounded-xl"
                        />
                    </div>
                </div>
                <div className="absolute top-4 right-4 bg-card rounded-lg shadow-lg p-3 animate-float hidden sm:block">
                    <Upload className="w-6 h-6 text-primary" />
                </div>
                <div className="absolute bottom-4 left-4 bg-card rounded-lg shadow-lg p-3 animate-float-delayed hidden sm:block">
                    <Users className="w-6 h-6 text-primary" />
                </div>
                </div>
            </div>
            </div>

            <div className="mb-12 sm:mb-16">
                <h3 className="text-2xl sm:text-3xl font-bold text-center mb-8 sm:mb-12">Core Features</h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 sm:gap-8">
                    {features.map((feature, index) => (
                    <div
                        key={index}
                        className="bg-card rounded-2xl p-6 sm:p-8 border border-border hover:border-primary/50 transition-all hover:shadow-lg group"
                    >
                        <div className="bg-primary/10 rounded-xl p-3 w-fit mb-4 group-hover:bg-primary/20 transition-colors">
                        <feature.icon className="w-6 h-6 sm:w-7 sm:h-7 text-primary" />
                        </div>
                        <h3 className="text-lg sm:text-xl font-semibold mb-2 group-hover:text-primary transition-colors">
                        {feature.title}
                        </h3>
                        <p className="text-sm sm:text-base text-muted-foreground leading-relaxed">
                        {feature.description}
                        </p>
                    </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default memo(HeroSection);