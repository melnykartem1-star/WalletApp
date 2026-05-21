import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

const ICONS = ['🛒', '🚗', '🏠', '🎮', '🏥', '🎓', '✈️', '🍔', '👗', '💼', '🎁', '💡', '💰', '🔄', '🏦'];

export default function EditCategory() {
    const { t } = useTranslation();
    const { id } = useParams();
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [type, setType] = useState('EXPENSE');
    const [color, setColor] = useState('#4CAF50');
    const [icon, setIcon] = useState('🛒');
    const navigate = useNavigate();

    useEffect(() => {
        axiosInstance.get(`/categories/${id}`).then(res => {
            setTitle(res.data.title);
            setDescription(res.data.description || '');
            setType(res.data.type);
            setColor(res.data.color || '#4CAF50');
            setIcon(res.data.icon || '🛒');
        }).catch(() => {
            toast.error(t('categories.edit_error_load', 'Категорію не знайдено'));
            navigate('/categories');
        });
    }, [id, navigate, t]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axiosInstance.patch(`/categories/${id}`, { title, description, type, color, icon });
            toast.success(t('categories.edit_success', 'Категорію оновлено!'));
            navigate('/categories');
        } catch (error: any) {
            toast.error(error.response?.data?.message || t('common.error', 'Помилка'));
        }
    };

    const handleDelete = async () => {
        const isConfirmed = window.confirm(t('categories.confirm_delete', 'Видалити цю категорію?'));
        if (!isConfirmed) return;

        try {
            await axiosInstance.delete(`/categories/${id}`);
            toast.success(t('categories.delete_success', 'Категорію видалено'));
            navigate('/categories');
        } catch (error: any) {
            toast.error(error.response?.data?.message || t('categories.delete_error', 'Помилка при видаленні'));
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh', width: '100%' }}>
            <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '600px', width: '100%', padding: '20px' }}>
                <h2 style={{ marginBottom: '20px', textAlign: 'center' }}>{t('categories.edit_title', 'Редагування категорії')}</h2>
                
                <form onSubmit={handleSubmit} style={{ 
                    display: 'flex', flexDirection: 'column', gap: '15px', 
                    background: '#1e1e1e', padding: '25px', borderRadius: '8px', 
                    border: '1px solid #333', boxShadow: '0 4px 15px rgba(0,0,0,0.3)' 
                }}>
                    <input 
                        type="text" 
                        placeholder={t('categories.name', 'Назва')} 
                        value={title} 
                        onChange={e => setTitle(e.target.value)} 
                        required 
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} 
                    />
                    
                    <textarea 
                        placeholder={t('categories.description', 'Опис')} 
                        value={description} 
                        onChange={e => setDescription(e.target.value)} 
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', minHeight: '80px', resize: 'none' }} 
                    />
                    
                    <select 
                        value={type} 
                        onChange={e => setType(e.target.value)} 
                        style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}
                    >
                        <option value="EXPENSE">{t('categories.types.EXPENSE', 'Витрата')}</option>
                        <option value="INCOME">{t('categories.types.INCOME', 'Дохід')}</option>
                        <option value="TRANSFER">{t('categories.types.TRANSFER', 'Переказ')}</option>
                    </select>

                    <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                            <label style={{ fontSize: '14px', color: '#aaa' }}>{t('categories.color', 'Колір')}</label>
                            <input type="color" value={color} onChange={e => setColor(e.target.value)} style={{ width: '50px', height: '40px', cursor: 'pointer', background: 'transparent', border: 'none' }} />
                        </div>
                        
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px', flex: 1 }}>
                            <label style={{ fontSize: '14px', color: '#aaa' }}>{t('categories.icon', 'Іконка')}</label>
                            <select value={icon} onChange={e => setIcon(e.target.value)} style={{ padding: '10px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', fontSize: '18px', cursor: 'pointer' }}>
                                {ICONS.map(i => <option key={i} value={i}>{i}</option>)}
                            </select>
                        </div>
                    </div>

                    <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                        <button type="submit" style={{ flex: 1, padding: '12px', background: '#1976d2', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
                            {t('common.save', 'Зберегти')}
                        </button>
                        <button type="button" onClick={() => navigate('/categories')} style={{ flex: 1, padding: '12px', background: '#444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>
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