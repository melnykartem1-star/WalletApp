import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import translationEN from './locales/en.json';
import translationUK from './locales/uk.json';

const savedLocale = localStorage.getItem('locale') || 'uk';

i18n.use(initReactI18next).init({
  resources: {
    en: { translation: translationEN },
    uk: { translation: translationUK }
  },
  lng: savedLocale,
  fallbackLng: 'uk',
  interpolation: { escapeValue: false }
});

i18n.on('languageChanged', (lng) => {
  document.documentElement.lang = lng;
});

export default i18n;