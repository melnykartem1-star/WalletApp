import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

export default function EditMerchant() {
    const { t } = useTranslation();
    const { id } = useParams();
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [icon, setIcon] = useState('');
    const [categoryId, setCategoryId] = useState('');
    
    // Стейт для списку категорій у випадаючому списку
    const [categories, setCategories] = useState<any[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Завантажуємо паралельно продавця і список категорій
                const [merchantRes, categoriesRes] = await Promise.all([
                    axiosInstance.get(`/merchants/${id}`),
                    axiosInstance.get('/categories')
                ]);

                const merchant = merchantRes.data;
                setName(merchant.name);
                setIcon(merchant.icon || '');
                setCategoryId(merchant.category ? merchant.category.id.toString() : '');
                
                // Обробляємо список категорій (якщо він з пагінацією чи без)
                setCategories(categoriesRes.data?.content || categoriesRes.data || []);
            } catch (error) {
                toast.error(t('merchants.edit_error_load', 'Не вдалося завантажити дані продавця.'));
                navigate('/merchants');
            }
        };
        fetchData();
    }, [id, navigate, t]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axiosInstance.patch(`/merchants/${id}`, { 
                name, 
                icon, 
                categoryId: categoryId ? parseInt(categoryId) : null 
            });
            toast.success(t('merchants.edit_success', 'Продавця успішно оновлено.'));
            navigate('/merchants');
        } catch (error: any) {
            toast.error(error.response?.data?.message || t('merchants.edit_error_update', 'Помилка при оновленні.'));
        }
    };

    const handleDelete = async () => {
        const isConfirmed = window.confirm(t('merchants.confirm_delete', 'Видалити цього продавця?'));
        if (!isConfirmed) return;

        try {
            await axiosInstance.delete(`/merchants/${id}`);
            toast.success(t('merchants.delete_success', 'Продавця видалено.'));
            navigate('/merchants');
        } catch (error: any) {
            toast.error(error.response?.data?.message || t('merchants.delete_error', 'Помилка при видаленні.'));
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh', width: '100%' }}>
            <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '600px', width: '100%', padding: '20px' }}>
                <h2 style={{ marginBottom: '20px', textAlign: 'center' }}>{t('merchants.edit_title', 'Редагувати продавця')}</h2>
                
                <form onSubmit={handleSubmit} style={{ 
                    display: 'flex', flexDirection: 'column', gap: '15px', 
                    background: '#1e1e1e', padding: '25px', borderRadius: '8px', 
                    border: '1px solid #333', boxShadow: '0 4px 15px rgba(0,0,0,0.3)' 
                }}>
                    
                    {/* Назва */}
                    <input 
                        type="text" 
                        placeholder={t('merchants.name', 'Назва')} 
                        value={name} 
                        onChange={e => setName(e.target.value)} 
                        required 
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} 
                    />
                    
                    {/* Іконка */}
                    <input 
                        type="text" 
                        placeholder={t('categories.icon', 'Іконка (напр. 🏪)')} 
                        value={icon} 
                        onChange={e => setIcon(e.target.value)} 
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} 
                    />

                    {/* Вибір категорії */}
                    <select 
                        value={categoryId} 
                        onChange={e => setCategoryId(e.target.value)} 
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}
                    >
                        <option value="">{t('merchants.select_category', 'Оберіть категорію (необов\'язково)')}</option>
                        {categories.map(c => (
                            <option key={c.id} value={c.id}>{c.title}</option>
                        ))}
                    </select>

                    {/* Кнопки збереження/скасування */}
                    <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                        <button type="submit" style={{ flex: 1, padding: '12px', background: '#1976d2', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
                            {t('common.save', 'Зберегти')}
                        </button>
                        <button type="button" onClick={() => navigate('/merchants')} style={{ flex: 1, padding: '12px', background: '#444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>
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