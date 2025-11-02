import { useEffect, useState } from "react";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { toast } from "sonner";
import { useDialog } from "@/hooks/useDialog";
import { PasswordInput } from "../PasswordInput";
import { useSocialLogin } from "@/hooks/useSocialLogin";

const SignupDialog = () => {
    const { dialog, closeDialog, openDialog } = useDialog();
    const initFormBirth = {
        birthDay: "",
        birthMonth: "",
        birthYear: ""
    };
    const initFormData = {
        firstName: "",
        lastName: "",
        dateOfBirth: "",
        email: "",
        password: ""
    };

    const [formBirth, setFormBirth] = useState(initFormBirth);
    const [formData, setFormData] = useState(initFormData);

    const months = [
        { value: "1", label: "January" },
        { value: "2", label: "February" },
        { value: "3", label: "March" },
        { value: "4", label: "April" },
        { value: "5", label: "May" },
        { value: "6", label: "June" },
        { value: "7", label: "July" },
        { value: "8", label: "August" },
        { value: "9", label: "September" },
        { value: "10", label: "October" },
        { value: "11", label: "November" },
        { value: "12", label: "December" },
    ];

    const currentYear = new Date().getFullYear();
    const years = Array.from({ length: 120 }, (_, i) => currentYear - i);
    const daysInMonth =
        formBirth.birthMonth
        ? new Date(
              Number(formBirth.birthYear),
              Number(formBirth.birthMonth),
              0
          ).getDate()
        : 31;
    const days = Array.from({ length: daysInMonth }, (_, i) => i + 1);

    const handleChangeBirth = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormBirth(prev => ({
            ...prev,
            [name]: value,
        }));
    };

    useEffect(() => {
        if (formBirth.birthDay && formBirth.birthMonth && formBirth.birthYear) {
            const dob = `${formBirth.birthYear}-${formBirth.birthMonth.padStart(2,"0")}-${formBirth.birthDay.padStart(2,"0")}`;
            setFormData(prev => ({
                ...prev,
                dateOfBirth: dob,
            }));
        }
    }, [formBirth]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleCloseDialog = () => {
        setFormData(initFormData);
        closeDialog();
    }

    const handleSignup = async (e: React.FormEvent) => {
        e.preventDefault();
        const token = await recaptchaRef.current?.executeAsync();
        console.log(formData);
        // closeDialog();
    };

    const handleSocialSignup = () => {
        useSocialLogin();
    };

    return (
        <Dialog open={dialog === "signup"} onOpenChange={handleCloseDialog}>
            <DialogContent className="sm:max-w-125 max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-2xl font-bold text-center">Sign Up</DialogTitle>
                    <DialogDescription className="text-center">
                        Create a new account to start chatting
                    </DialogDescription>
                </DialogHeader>
                
                <form onSubmit={handleSignup} className="space-y-4 mt-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="firstName">First Name</Label>
                            <Input
                                id="firstName"
                                name="firstName"
                                type="text"
                                placeholder="Enter first name"
                                value={formData.firstName}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        
                        <div className="space-y-2">
                            <Label htmlFor="lastName">Last Name</Label>
                            <Input
                                id="lastName"
                                name="lastName"
                                type="text"
                                placeholder="Enter last name"
                                value={formData.lastName}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label>Date of Birth</Label>
                        <div className="grid grid-cols-3 gap-2">
                            <select
                                id="birthMonth"
                                name="birthMonth"
                                value={formBirth.birthMonth}
                                onChange={handleChangeBirth}
                                required
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                                <option value="">Month</option>
                                {months.map((month) => (
                                    <option key={month.value} value={month.value}>
                                        {month.label}
                                    </option>
                                ))}
                            </select>

                            <select
                                id="birthDay"
                                name="birthDay"
                                value={formBirth.birthDay}
                                onChange={handleChangeBirth}
                                required
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                                <option value="">Day</option>
                                {days.map((day) => (
                                    <option key={day} value={day}>
                                        {day}
                                    </option>
                                ))}
                            </select>

                            <select
                                id="birthYear"
                                name="birthYear"
                                value={formBirth.birthYear}
                                onChange={handleChangeBirth}
                                required
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                                <option value="">Year</option>
                                {years.map((year) => (
                                    <option key={year} value={year}>
                                        {year}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>
                
                    <div className="space-y-2">
                        <Label htmlFor="email">Email Address</Label>
                        <Input
                            id="email"
                            name="email"
                            type="email"
                            placeholder="Enter email address"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>
                
                    <div className="space-y-2">
                        <PasswordInput
                            value={formData.password}
                            onChange={handleChange}
                            id="password"
                            name="password"
                            label="Password"
                            placeholder="Enter password (min 8 characters)"
                        />
                    </div>

                    <Button type="submit" className="w-full hover:bg-blue-600 transition" size="lg">
                        Sign Up
                    </Button>
                </form>

                <div className="relative my-4">
                    <Separator />
                    <span className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 bg-background px-2 text-xs text-muted-foreground">
                        Or continue with
                    </span>
                </div>

                <div className="grid">
                    <Button
                        type="button"
                        variant="outline"
                        size="lg"
                        onClick={() => handleSocialSignup()}
                    >
                        <svg className="w-5 h-5 mr-2" viewBox="0 0 24 24">
                        <path
                            fill="currentColor"
                            d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                        />
                        <path
                            fill="currentColor"
                            d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                        />
                        <path
                            fill="currentColor"
                            d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                        />
                        <path
                            fill="currentColor"
                            d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                        />
                        </svg>
                        Google
                    </Button>
                </div>

                <p className="text-center text-sm text-muted-foreground mt-4">
                    Already have an account?{" "}
                    <button
                        type="button"
                        onClick={() => openDialog("login")}
                        className="text-primary hover:underline font-medium"
                    >
                        Login
                    </button>
                </p>
            </DialogContent>
        </Dialog>
    );
};

export default SignupDialog;
