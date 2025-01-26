import { useEffect, useState } from 'react';
import { Keyboard } from 'react-native';

export const useDetectKeyboardOpen = (defaultValue: boolean) => {
    const [isKeyboardOpen, setIsKeyboardOpen] = useState<boolean>(defaultValue);

    useEffect(() => {
        const keyboardDidShowListener = Keyboard.addListener('keyboardDidShow', () => {
            setIsKeyboardOpen(true);
        });

        const keyboardDidHideListener = Keyboard.addListener('keyboardDidHide', () => {
            setIsKeyboardOpen(false);
        });

        return () => {
            keyboardDidShowListener.remove();
            keyboardDidHideListener.remove();
        };
    }, []);

    return isKeyboardOpen;
};