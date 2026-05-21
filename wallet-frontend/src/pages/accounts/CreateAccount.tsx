import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

export default function CreateAccount() {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [currency, setCurrency] = useState('UAH'); // За замовчуванням Гривня
    const [type, setType] = useState('CASH'); // За замовчуванням Готівка

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axiosInstance.post('/accounts', { 
                title, 
                description: description || null, // Відправляємо null, якщо порожньо
                currency, 
                type 
            });
            toast.success(t('accounts.create_success', 'Рахунок створено!'));
            navigate('/accounts');
        } catch (error: any) {
            toast.error(error.response?.data?.message || t('accounts.create_error', 'Помилка при створенні.'));
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh', width: '100%' }}>
            <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '600px', width: '100%', padding: '20px' }}>
                <h2 style={{ marginBottom: '20px', textAlign: 'center' }}>{t('accounts.new_title', 'Новий рахунок')}</h2>
                
                <form onSubmit={handleSubmit} style={{ 
                    display: 'flex', flexDirection: 'column', gap: '15px', 
                    background: '#1e1e1e', padding: '25px', borderRadius: '8px', 
                    border: '1px solid #333', boxShadow: '0 4px 15px rgba(0,0,0,0.3)' 
                }}>
                    
                    {/* Назва */}
                    <input 
                        type="text" 
                        placeholder={t('accounts.create_title', 'Назва')} 
                        value={title} 
                        onChange={e => setTitle(e.target.value)} 
                        required 
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} 
                    />

                    <select 
                        value={currency} 
                        onChange={e => setCurrency(e.target.value)} 
                        required
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}
                    >
                        <option value="UAH">{t('accounts.currency_uah', 'Гривня (UAH)')}</option>
                        <option value="USD">{t('accounts.currency_usd', 'Долар США (USD)')}</option>
                        <option value="EUR">{t('accounts.currency_eur', 'Євро (EUR)')}</option>
                    </select>

                    <select 
                        value={type} 
                        onChange={e => setType(e.target.value)} 
                        required
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}
                    >
                        <option value="CASH">{t('accounts.type_cash', 'Готівка')}</option>
                        <option value="CARD">{t('accounts.type_card', 'Картка')}</option>
                        <option value="BANK_ACCOUNT">{t('accounts.type_bank', 'Банківський рахунок')}</option>
                        <option value="CRYPTO">{t('accounts.type_crypto', 'Криптовалюта')}</option>
                    </select>

                    {/* Опис */}
                    <textarea 
                        placeholder={t('accounts.description', 'Опис')} 
                        value={description} 
                        onChange={e => setDescription(e.target.value)} 
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', minHeight: '80px', resize: 'none' }} 
                    />

                    {/* Кнопки */}
                    <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                        <button type="submit" style={{ flex: 1, padding: '12px', background: '#4CAF50', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
                            {t('common.create', 'Створити')}
                        </button>
                        <button type="button" onClick={() => navigate('/accounts')} style={{ padding: '12px', background: '#444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>
                            {t('common.cancel', 'Скасувати')}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}