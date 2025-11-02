import { useEffect, useState, useRef } from "react";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { useDialog } from "@/hooks/useDialog";
import { PasswordInput } from "../PasswordInput";
import { useSocialLogin } from "@/hooks/useSocialLogin";
import { ERROR_MESSAGE } from "@/utils/errorMessage";
import { getAge } from "@/utils/getAge";
import RecaptchaInv from "../recaptcha/RecaptchaInv";
import type ReCAPTCHA from "react-google-recaptcha";
import AccountService from "@/services/AccountService";
import { toast } from "sonner";
import { Loader2 } from "lucide-react";
import { nameRegex } from "@/utils/nameRegex";
import { emailRegex } from "@/utils/emailRegex";

const SignupDialog = () => {
    const { dialog, closeDialog, openDialog } = useDialog();
    const recaptchaRef = useRef<ReCAPTCHA>(null);
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
        password: "",
        recaptcha: ""
    };
    const initMsgError = {
        firstName: "",
        lastName: "",
        dateOfBirth: "",
        email: "",
        password: "",
        general: ""
    };

    const [formBirth, setFormBirth] = useState(initFormBirth);
    const [formData, setFormData] = useState(initFormData);
    const [msgError, setMsgError] = useState(initMsgError);
    const [dateOfBirth, setDateOfBirth] = useState(new Date());
    const [isLoading, setIsLoading] = useState(false);
    const [disabled, setDisabled] = useState(true);

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

        setMsgError(prev => ({
            ...prev,
            dateOfBirth: "",
        }));
    };

    useEffect(() => {
        if (formBirth.birthDay && formBirth.birthMonth && formBirth.birthYear) {
            const dob = new Date(
                Number(formBirth.birthYear),
                Number(formBirth.birthMonth) - 1,
                Number(formBirth.birthDay)
            );
            const dobStr = `${formBirth.birthYear}-${formBirth.birthMonth.padStart(2,"0")}-${formBirth.birthDay.padStart(2,"0")}`;
        
            setDateOfBirth(dob);
            setFormData(prev => ({
                ...prev,
                dateOfBirth: dobStr,
            }));
        }
    }, [formBirth]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value.trim,
        }));

        setMsgError(prev => ({
            ...prev,
            general: "",
            [name]: ""
        }));
    };

    const handleCloseDialog = () => {
        setFormData(initFormData);
        setFormBirth(initFormBirth);
        setMsgError(initMsgError);
        setDisabled(false);
        setIsLoading(false);
        closeDialog();
    }

    useEffect(() => {
        if (formData.firstName && formData.lastName && formData.dateOfBirth && formData.email && formData.password) {
            setDisabled(false);
        } else {
            setDisabled(true);
        }
    }, [formData])

    const validateFormData = () => {
        let isValid = true;

        if (!nameRegex(formData.firstName)) {
            setMsgError(prev => ({
                ...prev,
                firstName: ERROR_MESSAGE.FIRSTNAME_INVALID
            }))

            isValid = false;
        } else if (formData.firstName.length < 1 || formData.firstName.length > 30) {
            setMsgError(prev => ({
                ...prev,
                firstName: ERROR_MESSAGE.FIRSTNAME_LENGTH_INVALID
            }))

            isValid = false;
        }

        if (!nameRegex(formData.lastName)) {
           setMsgError(prev => ({
                ...prev,
                lastName: ERROR_MESSAGE.LASTNAME_INVALID
            }))

            isValid = false;
        } else if (formData.lastName.length < 1 || formData.lastName.length > 50) {
            setMsgError(prev => ({
                ...prev,
                lastName: ERROR_MESSAGE.LASTNAME_LENGTH_INVALID
            }))

            isValid = false;
        }

        if (getAge(dateOfBirth) < 0) {
            setMsgError(prev => ({
                ...prev,
                dateOfBirth: ERROR_MESSAGE.DATE_OF_BIRTH_INVALID
            }))

            isValid = false;
        } else if (getAge(dateOfBirth) < 13) {
            setMsgError(prev => ({
                ...prev,
                dateOfBirth: ERROR_MESSAGE.USER_UNDER_13
            }))

            isValid = false;
        } else if (getAge(dateOfBirth) > 120) {
            setMsgError(prev => ({
                ...prev,
                dateOfBirth: ERROR_MESSAGE.DATE_OF_BIRTH_TOO_OLD
            }))

            isValid = false;
        }

        if (!emailRegex(formData.email)) {
            setMsgError(prev => ({
                ...prev,
                email: ERROR_MESSAGE.EMAIL_INVALID
            }))

            isValid = false;
        }

        if (formData.password.length < 8 || formData.password.length > 32) {
            setMsgError(prev => ({
                ...prev,
                password: ERROR_MESSAGE.PASSWORD_LENGTH_INVALID
            }))

            isValid = false;
        }

        return isValid;
    }

    const handleSignup = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!validateFormData()) {
            return;
        };
        
        setMsgError(initMsgError);
        setIsLoading(true);

        const recaptchaToken = await recaptchaRef.current?.executeAsync();
        if (!recaptchaToken) {
            setMsgError(prev => ({
                ...prev,
                general: ERROR_MESSAGE.RECAPTCHA_INVALID
            }))
            
            return;
        }

        try {
            const response = await AccountService.register({
                ...formData,
                recaptcha: recaptchaToken
            })

            if (response?.code === 0) {
                handleCloseDialog();
                toast.success(
                    <span>
                        Almost done! Check your email <strong>{response?.result?.email}</strong> to verify your account.
                    </span>
                )
            }
        } catch (error: any) {
            if (error?.data?.code === 1201) {
                setMsgError(prev => ({
                    ...prev,
                    email: error?.data?.message
                }));
            } else {
                setMsgError(prev => ({
                    ...prev,
                    general: error?.data?.message || ERROR_MESSAGE.UNKNOWN_ERROR
                }));
            }
        }

        setIsLoading(false);
    };

    const handleSocialSignup = () => {
        useSocialLogin();
    };

    return (
        <Dialog open={dialog === "signup"} onOpenChange={handleCloseDialog}>
            <DialogContent
                className="flex flex-col sm:max-w-125 max-h-[90vh] overflow-y-auto overflow-x-hidden"
                onInteractOutside={(e) => e.preventDefault()}
                onEscapeKeyDown={(e) => e.preventDefault()}
            >
                <div className="flex-1 mb-4">
                    <DialogHeader>
                        <DialogTitle className="text-2xl font-bold text-center">Sign Up</DialogTitle>
                        <DialogDescription className="text-center">
                            Create a new account to start chatting
                        </DialogDescription>
                    </DialogHeader>
                    
                    <form onSubmit={handleSignup} className="space-y-3 mt-4">
                        <div className="grid grid-cols-2 gap-x-4 gap-y-1">
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
                                    tabIndex={1}
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
                                    tabIndex={2}
                                />
                            </div>

                            <p className="col-span-2 text-xs text-red-500 font-medium">
                                {msgError.firstName || msgError.lastName || ""}
                            </p>
                        </div>

                        <div className="space-y-2">
                            <Label>Date of Birth</Label>
                            <div className="grid grid-cols-3 gap-2 mb-1">
                                <select
                                    id="birthMonth"
                                    name="birthMonth"
                                    value={formBirth.birthMonth}
                                    onChange={handleChangeBirth}
                                    required
                                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                                    tabIndex={3}
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
                                    tabIndex={4}
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
                                    tabIndex={5}
                                >
                                    <option value="">Year</option>
                                    {years.map((year) => (
                                        <option key={year} value={year}>
                                            {year}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <p className="col-span-2 text-xs text-red-500 font-medium">
                                {msgError.dateOfBirth || ""}
                            </p>
                        </div>
                    
                        <div className="space-y-2">
                            <Label htmlFor="email">Email Address</Label>
                            <Input
                                id="email"
                                name="email"
                                type="text"
                                className="mb-1"
                                placeholder="Enter email address"
                                value={formData.email}
                                onChange={handleChange}
                                required
                                tabIndex={6}
                            />

                            <p className="col-span-2 text-xs text-red-500 font-medium">
                                {msgError.email || ""}
                            </p>
                        </div>
                    
                        <div className="space-y-2">
                            <PasswordInput
                                value={formData.password}
                                onChange={handleChange}
                                id="password"
                                name="password"
                                className="mb-1"
                                label="Password"
                                placeholder="Enter password (between 8 and 32 characters)"
                                tabIndex={7}
                            />

                            <p className="col-span-2 text-xs text-red-500 font-medium">
                                {msgError.password || msgError.general || ""}
                            </p>
                        </div>

                        <Button type="submit" className="w-full hover:bg-blue-600 transition" size="lg" disabled={disabled || isLoading} tabIndex={8}>
                            {isLoading ?
                                <Loader2 className="w-4 h-4 animate-spin" />
                                : 'Sign Up'
                            }
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
                            tabIndex={9}
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
                        <span className="me-1">Already have an account?</span>
                        <button
                            type="button"
                            onClick={() => openDialog("login")}
                            className="text-primary hover:underline font-medium"
                        >
                            Login
                        </button>
                    </p>
                </div>
                
                <div className="mt-auto -z-10">
                    <RecaptchaInv ref={recaptchaRef}/>
                </div>
            </DialogContent>
        </Dialog>
    );
};

export default SignupDialog;
