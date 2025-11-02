export const getAge = (dob: Date): number => {
    const today = new Date();

    let age = today.getFullYear() - dob.getFullYear();

    if (
        today.getMonth() < dob.getMonth() || (today.getMonth() === dob.getMonth() && today.getDate() < dob.getDate())
    ) {
        age--;
    }

    return age;
};