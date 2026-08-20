let isAdmin = false;
let produtoAtual = null;
let csrfToken = null;

const numero = "5537998648499";
const grid = document.getElementById("produtos");
const gridProdutos = document.getElementById("produtos");
const setaDireita = document.getElementById("setaDireita");
const setaEsquerda = document.getElementById("setaEsquerda");
const menuMobile = document.getElementById("menuMobile");

function whatsapp() {
    const mensagem = "Ola! Vim pelo site dos Produtos Delicias da Roca e quero saber mais sobre as rapaduras artesanais.";
    const url = `https://wa.me/${numero}?text=${encodeURIComponent(mensagem)}`;
    window.open(url, "_blank");
}

async function carregarCsrf() {
    if (csrfToken) {
        return csrfToken;
    }

    const resposta = await fetch("/csrf");
    csrfToken = await resposta.json();
    return csrfToken;
}

async function fetchComCsrf(url, options = {}) {
    const token = await carregarCsrf();

    return fetch(url, {
        ...options,
        headers: {
            ...(options.headers || {}),
            [token.headerName]: token.token
        }
    });
}

async function verificarAdmin() {
    try {
        const resposta = await fetch("/admin/status");
        const status = await resposta.json();
        isAdmin = Boolean(status.admin);
    } catch (error) {
        isAdmin = false;
    }

    const adminDesktopBtn = document.getElementById("adminDesktopBtn");
    if (adminDesktopBtn) {
        adminDesktopBtn.style.display = isAdmin ? "inline-block" : "none";
    }

    const adminMobileBtn = document.getElementById("adminMobileBtn");
    if (adminMobileBtn) {
        adminMobileBtn.style.display = isAdmin ? "block" : "none";
    }
}

async function carregarProdutos() {
    if (!grid) {
        return;
    }

    try {
        const resposta = await fetch("/produtos");

        if (!resposta.ok) {
            throw new Error("Nao foi possivel carregar os produtos.");
        }

        const produtos = await resposta.json();
        grid.innerHTML = "";

        produtos.forEach((p) => {
            const codigo = p.codigo ?? "";
            const nome = p.nome ?? "";
            const descricao = p.descricao ?? "";
            const imagem = p.imagem ?? "";
            let adminButtons = "";

            if (isAdmin) {
                adminButtons = `
                    <div class="admin-actions">
                        <button class="edit-btn"
                            data-id="${escapeHtml(codigo)}"
                            data-nome="${encodeURIComponent(nome)}"
                            data-descricao="${encodeURIComponent(descricao)}"
                            data-imagem="${encodeURIComponent(imagem)}"
                            onclick="editarProdutoDataset(this)">
                            Editar
                        </button>
                        <button class="delete-btn" onclick="deletarProduto('${escapeHtml(codigo)}')">
                            Apagar
                        </button>
                    </div>
                `;
            }

            grid.innerHTML += `
                <div class="card">
                    <img src="${escapeHtml(imagem)}" alt="${escapeHtml(nome)}">
                    <div class="card-content">
                        <h3>${escapeHtml(nome)}</h3>
                        <p>${escapeHtml(descricao)}</p>
                        ${adminButtons}
                    </div>
                </div>
            `;
        });

        setTimeout(atualizarSetas, 100);
    } catch (error) {
        console.error(error);
        grid.innerHTML = "<p>Nao foi possivel carregar os sabores agora.</p>";
    }
}

function editarProdutoDataset(btn) {
    produtoAtual = btn.dataset.id;
    document.getElementById("editNome").value = decodeURIComponent(btn.dataset.nome);
    document.getElementById("editDescricao").value = decodeURIComponent(btn.dataset.descricao);
    document.getElementById("editImagem").value = decodeURIComponent(btn.dataset.imagem);
    document.getElementById("modalEditar").style.display = "flex";
}

function fecharModalEditar() {
    document.getElementById("modalEditar").style.display = "none";
}

async function salvarEdicao() {
    const nome = document.getElementById("editNome").value.trim();
    const descricao = document.getElementById("editDescricao").value.trim();
    const imagem = document.getElementById("editImagem").value.trim();

    const resposta = await fetchComCsrf(`/produtos/${produtoAtual}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ nome, descricao, imagem })
    });

    if (resposta.ok) {
        fecharModalEditar();
        await carregarProdutos();
    } else {
        alert("Nao foi possivel salvar a edicao.");
    }
}

function deletarProduto(id) {
    produtoAtual = id;
    document.getElementById("modalDelete").style.display = "flex";
}

function fecharModalDelete() {
    document.getElementById("modalDelete").style.display = "none";
}

async function confirmarDelete() {
    const resposta = await fetchComCsrf(`/produtos/${produtoAtual}`, {
        method: "DELETE"
    });

    if (resposta.ok) {
        fecharModalDelete();
        await carregarProdutos();
    } else {
        alert("Nao foi possivel apagar o sabor.");
    }
}

function atualizarSetas() {
    if (!gridProdutos || !setaDireita || !setaEsquerda) {
        return;
    }

    const maxScroll = gridProdutos.scrollWidth - gridProdutos.clientWidth;
    setaEsquerda.style.display = gridProdutos.scrollLeft > 10 ? "flex" : "none";
    setaDireita.style.display = gridProdutos.scrollLeft < maxScroll - 10 ? "flex" : "none";
}

function abrirMenu() {
    if (menuMobile) {
        menuMobile.classList.add("ativo");
    }
}

function fecharMenu() {
    if (menuMobile) {
        menuMobile.classList.remove("ativo");
    }
}

function escapeHtml(valor) {
    return String(valor)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

if (setaDireita && gridProdutos) {
    setaDireita.addEventListener("click", () => {
        gridProdutos.scrollBy({ left: 300, behavior: "smooth" });
    });
}

if (setaEsquerda && gridProdutos) {
    setaEsquerda.addEventListener("click", () => {
        gridProdutos.scrollBy({ left: -300, behavior: "smooth" });
    });
}

if (gridProdutos) {
    gridProdutos.addEventListener("scroll", atualizarSetas);
}

window.addEventListener("load", async () => {
    await verificarAdmin();
    await carregarProdutos();
    atualizarSetas();
});
