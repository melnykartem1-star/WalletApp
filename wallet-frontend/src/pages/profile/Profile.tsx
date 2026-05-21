import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

export default function Profile() {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    
    // 1. Одразу ініціалізуємо всі поля, щоб уникнути uncontrolled components
    const [profile, setProfile] = useState({ 
        name: '', 
        email: '', 
        locale: 'uk', 
        timezone: 'Europe/Kyiv' 
    });
    const [passwordData, setPasswordData] = useState({ oldPassword: '', newPassword: '' });

    useEffect(() => {
        axiosInstance.get('/users/profile')
            .then(res => {
                // 2. Якщо бекенд повернув null, ставимо дефолтні значення
                const fetchedLocale = res.data.locale || 'uk';
                const fetchedTimezone = res.data.timezone || 'Europe/Kyiv';
                
                setProfile({ 
                    name: res.data.name || '', 
                    email: res.data.email || '', 
                    locale: fetchedLocale,
                    timezone: fetchedTimezone
                });
                
                i18n.changeLanguage(fetchedLocale);
                localStorage.setItem('locale', fetchedLocale);
                document.documentElement.lang = fetchedLocale;
            })
            .catch(() => toast.error(t('profile.error_load', 'Помилка завантаження профілю')));
    // 3. ТУТ ВАЖЛИВО: пустий масив. Запит робиться лише один раз при вході на сторінку.
    }, []); 

    const handleProfileSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axiosInstance.patch('/users/profile', profile);
            toast.success(t('profile.success_update', 'Профіль успішно оновлено'));
            
            // Застосовуємо локаль глобально тільки після успішного збереження
            i18n.changeLanguage(profile.locale);
            localStorage.setItem('locale', profile.locale);
            document.documentElement.lang = profile.locale;
        } catch (err: any) {
            toast.error(err.response?.data?.message || t('profile.error_update', 'Помилка оновлення'));
        }
    };

    const handlePasswordSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axiosInstance.put('/users/password', passwordData);
            toast.success(t('profile.success_password', 'Пароль змінено'));
            setPasswordData({ oldPassword: '', newPassword: '' });
        } catch (err: any) {
            toast.error(err.response?.data?.message || 'Помилка оновлення пароля');
        }
    };

    const handleDeleteAccount = async () => {
        const confirmDelete = window.confirm(t('profile.confirm_delete', 'Видалити акаунт?'));
        if (!confirmDelete) return;

        try {
            await axiosInstance.delete('/users');
            localStorage.clear();
            navigate('/register');
        } catch (err: any) {
            toast.error(t('profile.error_delete', 'Помилка видалення'));
        }
    };

    return (
        <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '900px' }}>
            <h1 style={{ marginBottom: '30px', fontSize: '28px' }}>{t('menu.profile', 'Профіль')}</h1>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
                <div style={{ background: '#1e1e1e', padding: '25px', borderRadius: '8px', border: '1px solid #333' }}>
                    <h3 style={{ margin: '0 0 20px 0', color: '#fff' }}>{t('profile.basic_info', 'Базова інформація')}</h3>
                    
                    <form onSubmit={handleProfileSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                            <label style={{ color: '#aaa', fontSize: '14px' }}>{t('profile.name', 'Ім\'я')}</label>
                            <input 
                                type="text" 
                                value={profile.name} 
                                onChange={e => setProfile({ ...profile, name: e.target.value })} 
                                required 
                                style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} 
                            />
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                            <label style={{ color: '#aaa', fontSize: '14px' }}>{t('profile.email', 'Email')}</label>
                            <input 
                                type="email" 
                                value={profile.email} 
                                onChange={e => setProfile({ ...profile, email: e.target.value })} 
                                required 
                                style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} 
                            />
                        </div>
                        
                        {/* Зміна мови */}
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                            <label style={{ color: '#aaa', fontSize: '14px' }}>{t('profile.locale', 'Мова інтерфейсу')}</label>
                            <select 
                                value={profile.locale} 
                                onChange={e => {
                                    const newLocale = e.target.value;
                                    setProfile({ ...profile, locale: newLocale });
                                    // Миттєво застосовуємо мову для візуалу, 
                                    // але бекенд не знає про це до натискання "Зберегти"
                                    i18n.changeLanguage(newLocale);
                                    document.documentElement.lang = newLocale;
                                }} 
                                style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}
                            >
                                <option value="uk">Українська</option>
                                <option value="en">English</option>
                            </select>
                        </div>
                        

                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                            <label style={{ color: '#aaa', fontSize: '14px' }}>{t('profile.timezone', 'Часовий пояс')}</label>
                            <select 
                                value={profile.timezone} 
                                onChange={e => setProfile({ ...profile, timezone: e.target.value })} 
                                style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}
                            >
                                <option value="UTC">{t('profile.timezones.utc', 'UTC')}</option>
    <option value="Europe/Kyiv">{t('timezones.kyiv')}</option>
    <option value="Europe/Warsaw">{t('timezones.warsaw')}</option>
    <option value="Europe/London">{t('timezones.london')}</option>
    <option value="America/New_York">{t('timezones.new_york')}</option>
    <option value="Asia/Tokyo">{t('timezones.tokyo')}</option>
                            </select>
                        </div>

                        <button type="submit" style={{ padding: '12px', background: '#4CAF50', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', marginTop: '10px' }}>
                            {t('common.save', 'Зберегти')}
                        </button>
                    </form>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '30px' }}>
                    <div style={{ background: '#1e1e1e', padding: '25px', borderRadius: '8px', border: '1px solid #333' }}>
                        <h3 style={{ margin: '0 0 20px 0', color: '#fff' }}>{t('profile.security', 'Безпека')}</h3>
                        <form onSubmit={handlePasswordSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                                <label style={{ color: '#aaa', fontSize: '14px' }}>{t('profile.current_password', 'Поточний пароль')}</label>
                                <input type="password" value={passwordData.oldPassword} onChange={e => setPasswordData({ ...passwordData, oldPassword: e.target.value })} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} />
                            </div>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                                <label style={{ color: '#aaa', fontSize: '14px' }}>{t('profile.new_password', 'Новий пароль')}</label>
                                <input type="password" value={passwordData.newPassword} onChange={e => setPasswordData({ ...passwordData, newPassword: e.target.value })} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} />
                            </div>
                            <button type="submit" style={{ padding: '12px', background: '#1976d2', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', marginTop: '10px' }}>
                                {t('profile.update_password', 'Оновити пароль')}
                            </button>
                        </form>
                    </div>

                    <div style={{ background: '#2c1111', padding: '25px', borderRadius: '8px', border: '1px solid #ef5350' }}>
                        <h3 style={{ margin: '0 0 10px 0', color: '#ef5350' }}>{t('profile.danger_zone', 'Небезпечна зона')}</h3>
                        <p style={{ color: '#aaa', fontSize: '14px', marginBottom: '20px', lineHeight: '1.5' }}>
                            {t('profile.danger_description', 'Видалення акаунта є незворотнім.')}
                        </p>
                        <button onClick={handleDeleteAccount} style={{ padding: '12px', width: '100%', background: '#d32f2f', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
                            {t('profile.delete_account', 'Видалити акаунт')}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}