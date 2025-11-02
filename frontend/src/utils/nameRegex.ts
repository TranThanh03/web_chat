export const nameRegex = (name: string): boolean => {
    const regex = /^[A-Za-z\s]+$/;

    if (regex.test(name)) {
        return true;
    }
    
    return false;
};