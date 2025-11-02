import { MessageCircle, Github, Linkedin, Facebook } from "lucide-react";
import { memo } from "react";

const Footer = () => {
    return (
        <footer className="bg-card border-t border-border mt-6 sm:mt-10">
            <div className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl py-8 sm:py-12">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
                    <div className="space-y-4">
                        <div className="flex items-center gap-2">
                            <div className="bg-primary rounded-xl p-2">
                                <MessageCircle className="w-5 h-5 text-primary-foreground" />
                            </div>
                            <h3 className="text-lg font-bold">Chat</h3>
                        </div>
                        <p className="text-sm text-muted-foreground leading-relaxed">
                            Modern real-time chat application, connecting people anytime, anywhere.
                        </p>
                    </div>

                    <div>
                        <h4 className="font-semibold mb-4">Product</h4>
                        <ul className="space-y-2 text-sm text-muted-foreground">
                            <li><a href="#" className="hover:text-primary transition-colors">Features</a></li>
                            <li><a href="#" className="hover:text-primary transition-colors">Security</a></li>
                            <li><a href="#" className="hover:text-primary transition-colors">Enterprise</a></li>
                            <li><a href="#" className="hover:text-primary transition-colors">Pricing</a></li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="font-semibold mb-4">Company</h4>
                        <ul className="space-y-2 text-sm text-muted-foreground">
                            <li><a href="#" className="hover:text-primary transition-colors">About Us</a></li>
                            <li><a href="#" className="hover:text-primary transition-colors">Blog</a></li>
                            <li><a href="#" className="hover:text-primary transition-colors">Careers</a></li>
                            <li><a href="#" className="hover:text-primary transition-colors">Contact</a></li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="font-semibold mb-4">Legal</h4>
                        <ul className="space-y-2 text-sm text-muted-foreground">
                            <li><a href="#" className="hover:text-primary transition-colors">Terms</a></li>
                            <li><a href="#" className="hover:text-primary transition-colors">Privacy</a></li>
                            <li><a href="#" className="hover:text-primary transition-colors">Cookies</a></li>
                            <li><a href="#" className="hover:text-primary transition-colors">License</a></li>
                        </ul>
                    </div>
                </div>

                <div className="border-t border-border mt-8 pt-8 flex flex-col sm:flex-row justify-between items-center gap-4">
                    <p className="text-sm text-muted-foreground text-center sm:text-left">
                        © 2025 Chat. All rights reserved.
                    </p>
                    
                    <div className="flex items-center gap-4">
                        <a href="#" className="text-muted-foreground hover:text-primary transition-colors">
                            <Facebook className="w-5 h-5" />
                        </a>
                        <a href="#" className="text-muted-foreground hover:text-primary transition-colors">
                            <Linkedin className="w-5 h-5" />
                        </a>
                        <a href="#" className="text-muted-foreground hover:text-primary transition-colors">
                            <Github className="w-5 h-5" />
                        </a>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default memo(Footer);
