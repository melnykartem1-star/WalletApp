import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

export default function EditAccount() {
    const { t } = useTranslation();
    const { id } = useParams();
    const navigate = useNavigate();

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [currency, setCurrency] = useState('UAH'); // Встановимо UAH як fallback
    const [type, setType] = useState('CASH');

    useEffect(() => {
        axiosInstance.get(`/accounts/${id}`).then(res => {
            setTitle(res.data.title);
            setDescription(res.data.description || '');
            setCurrency(res.data.currency);
            setType(res.data.type);
        }).catch(() => {
            toast.error(t('common.error_load', 'Помилка завантаження даних'));
        });
    }, [id, t]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axiosInstance.patch(`/accounts/${id}`, { 
                title, 
                description: description || null, // Відправляємо null, якщо опис порожній
                currency, 
                type 
            });
            toast.success(t('accounts.edit_success', 'Рахунок оновлено!'));
            navigate('/accounts');
        } catch (error: any) {
            toast.error(error.response?.data?.message || t('accounts.edit_error', 'Помилка при оновленні.'));
        }
    };

    const handleDelete = async () => {
        const isConfirmed = window.confirm(t('accounts.confirm_delete', 'Видалити цей рахунок?'));
        if (!isConfirmed) return;

        try {
            await axiosInstance.delete(`/accounts/${id}`);
            toast.success(t('accounts.delete_success', 'Рахунок видалено!'));
            navigate('/accounts');
        } catch (error: any) {
            toast.error(error.response?.data?.message || t('accounts.delete_error', 'Помилка при видаленні.'));
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh', width: '100%' }}>
            <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '600px', width: '100%', padding: '20px' }}>
                <h2 style={{ marginBottom: '20px', textAlign: 'center' }}>{t('accounts.edit_title', 'Редагування рахунку')}</h2>
                
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

                    {/* Валюта */}
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

                    {/* Тип рахунку */}
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
                        <button type="submit" style={{ flex: 1, padding: '12px', background: '#1976d2', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
                            {t('common.save', 'Зберегти')}
                        </button>
                        <button type="button" onClick={() => navigate('/accounts')} style={{ flex: 1, padding: '12px', background: '#444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>
                            {t('common.cancel', 'Скасувати')}
                        </button>
                    </div>

                    {/* Кнопка видалення */}
                    <button 
                        type="button" 
                        onClick={handleDelete} 
                        style={{ 
                            width: '100%', 
                            padding: '12px', 
                            background: 'transparent', 
                            color: '#ef5350', 
                            border: '1px solid #ef5350', 
                            borderRadius: '6px', 
                            cursor: 'pointer', 
                            fontWeight: 'bold',
                            marginTop: '5px'
                        }}
                    >
                        {t('common.delete', 'Видалити')}
                    </button>

                </form>
            </div>
        </div>
    );
}